/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4CTemplateHelper;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeOfGenerics;
import umlp.UMLPMill;
import umlp.generator.util.PathUtil;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DeSerDecorator extends DomainClassDecorator {

  public static final String SUFFIX = "DeSer";

  public DeSerDecorator(GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String cdName = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList()
            .stream()
            .filter(cdclass -> !cdclass.getModifier().isDerived())
            .forEach(cdClass -> astcdPackage.addCDElement(createDeSer(origCD, cdName, cdClass)));
  }

  protected ASTCDElement createDeSer(ASTCDCompilationUnit origCD, String cdName, ASTCDClass cdClass) {
    String originalClassName = cdClass.getName();

    ASTCDClass deSer = UMLPMill.cDClassBuilder()
            .setName(cdClass.getName() + SUFFIX)
            .setModifier(CDModifier.PUBLIC.build())
            .build();

    addImports(deSer);

    // TODO: also use for other templates!
    List<ASTCDAttribute> attributes = CDSymbolTables.getAttributesInHierarchy(cdClass).stream()
            .filter(attr -> !attr.getModifier().isDerived())
            .collect(Collectors.toList());
    attributes.forEach(a -> {
      addSerAndDeserMethod(originalClassName, deSer, a);
    });
    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociationsInHierarchy(cdClass).stream()
            .filter(a -> !a.getModifier().isDerived())
            .collect(Collectors.toList());
    assocs.forEach(a -> {
      addSerAndDeserMethod(cdName, originalClassName, deSer, a);
    });


    getCD4C().addMethod(deSer, "umlp.deser.serialize", originalClassName);
    getCD4C().addMethod(deSer, "umlp.deser.deserialize",
            cdName,
            originalClassName,
            attributes.stream().map(a -> a.getName()).collect(Collectors.toList()),
            assocs.stream().filter(a -> !GenService.isList(a)).map(a -> a.getName()).collect(Collectors.toList()),
            assocs.stream().filter(a -> GenService.isList(a)).map(a -> a.getName()).collect(Collectors.toList())
                       );
    getCD4C().addMethod(deSer, "umlp.deser.BuildJson",
            originalClassName,
            attributes.stream().map(a -> a.getName()).collect(Collectors.toList()),
            assocs.stream().map(a -> a.getName()).collect(Collectors.toList())
                       );

    getCD4C().addMethod(deSer, "umlp.deser.FromJson", originalClassName);


    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.method("protected void serialize_Addons(" + cdClass.getName() + " obj, JsonPrinter jsonPrinter)");
    deSer.addCDMember(th.getMethod().get());
    th.method("protected void deserialize_Addons(" + cdClass.getName() + "Builder builder, JsonObject json)");
    deSer.addCDMember(th.getMethod().get());


    return deSer;
  }

  protected void addSerAndDeserMethod(String originalClassName, ASTCDClass deSer, ASTCDAttribute attr) {
    boolean isList = false;
    boolean isOptional = false;
    String type = attr.getSymbol().getType().printFullName();
    String jsonStr = "";
    if (GenService.isList(attr)) {
      isList = true;
      jsonStr = GenService.calculateJSon(((SymTypeOfGenerics) attr.getSymbol().getType()).getArgument(0));
    } else if (GenService.isOptional(attr)) {
      isOptional = true;
      jsonStr = GenService.calculateJSon(((SymTypeOfGenerics) attr.getSymbol().getType()).getArgument(0));
    } else {
      jsonStr = GenService.calculateJSon(attr.getSymbol().getType());
    }
    getCD4C().addMethod(deSer, "umlp.deser.serializeSimpleField",
            originalClassName, attr.getName(), jsonStr, isList, isOptional);
    getCD4C().addMethod(deSer, "umlp.deser.deserializeSimpleField",
            originalClassName, attr.getName(), type, jsonStr, isList, isOptional);
  }

  protected void addSerAndDeserMethod(String cdName, String originalClassName, ASTCDClass deSer, ASTCDAssocSide attr) {
    if (GenService.isList(attr)) {
      getCD4C().addMethod(deSer, "umlp.deser.serializeGemListField", originalClassName, attr.getName());
      getCD4C().addMethod(deSer, "umlp.deser.deserializeGemListField", cdName, originalClassName, attr.getName());
    } else {
      getCD4C().addMethod(deSer, "umlp.deser.serializeGemField", originalClassName, attr.getName());
      getCD4C().addMethod(
              deSer,
              "umlp.deser.deserializeGemField",
              cdName,
              originalClassName,
              attr.getName(),
              attr.getSymbol().getType().print());
    }
  }

  @Override
  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.JsonPrinter");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonObject");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.JsonElement");
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("Gem"));
  }

}

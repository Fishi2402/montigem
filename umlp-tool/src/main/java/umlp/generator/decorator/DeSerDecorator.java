package umlp.generator.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4CTemplateHelper;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import umlp.UMLPMill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeSerDecorator extends DomainClassDecorator{
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
        .forEach(cdClass -> astcdPackage.addCDElement(createDeSer(cdName, cdClass)));
  }

  protected ASTCDElement createDeSer(String cdName, ASTCDClass cdClass) {
    String originalClassName = cdClass.getName();

    // TODO: ser and deser gemid!
    ASTCDClass deSer = UMLPMill.cDClassBuilder()
        .setName(cdClass.getName() + "DeSer")
        .setModifier(CDModifier.PUBLIC.build())
        .build();

    addImports(deSer);

    // Attributes resulting from class attributes and associations
    List<ASTCDAttribute> attrList = getAllAttributes(cdClass).attrList.stream()
        // TODO: Attributes of all types!
        .filter(attr -> isSimpleType(attr.printType()) || GenService.isGem(attr))
        .collect(Collectors.toList());

    attrList = attrList.stream().filter(attr -> !attr.getModifier().isDerived()).collect(Collectors.toList());

    attrList.forEach(attr -> {
          addSerMethod(originalClassName, deSer, attr);
          addDeserMethod(cdName, originalClassName, deSer, attr);
        });

    // TODO: also use for other templates!
    List<ASTCDAttribute> simpleAttributes = new ArrayList<>();
    List<ASTCDAttribute> gemAttributes = new ArrayList<>();
    List<ASTCDAttribute> gemListAttributes = new ArrayList<>();

    for (ASTCDAttribute attr : attrList) {
      if(isSimpleType(attr.printType())){
        simpleAttributes.add(attr);
      } else if(GenService.isGemList(attr)){
        gemListAttributes.add(attr);
      }else if(GenService.isGem(attr)){
        gemAttributes.add(attr);
      }
    }

    getCD4C().addMethod(deSer, "umlp.deser.serialize", originalClassName, attrList);
    getCD4C().addMethod(deSer, "umlp.deser.deserialize",
      cdName,
      originalClassName,
      simpleAttributes,
      gemAttributes,
      gemListAttributes
    );

    CD4CTemplateHelper th = new CD4CTemplateHelper();
    th.method("protected void serialize_Addons(" + cdClass.getName() + " obj, JsonPrinter jsonPrinter)");
    deSer.addCDMember(th.getMethod().get());
    th.method("protected void deserialize_Addons(" + cdClass.getName() + "Builder builder, JsonObject json)");
    deSer.addCDMember(th.getMethod().get());


    return deSer;
  }

  protected void addSerMethod(String originalClassName, ASTCDClass deSer, ASTCDAttribute attr) {
    if(GenService.isGemList(attr)){
      getCD4C().addMethod(deSer, "umlp.deser.serializeGemListField", originalClassName, attr.getName());
    } else if(GenService.isGem(attr)){
      getCD4C().addMethod(deSer, "umlp.deser.serializeGemField", originalClassName, attr.getName());
    } else if(GenService.isList(attr)) {
      // TODO
    } else if(GenService.isOptional(attr)) {
      // TODO
    } else {
      // TODO: Other simple types!
      if(isSimpleType(attr.printType())) {
        getCD4C().addMethod(deSer, "umlp.deser.serializeSimpleField", originalClassName, attr.getName());
      }
    }
  }

  protected void addDeserMethod(String cdName, String originalClassName, ASTCDClass deSer, ASTCDAttribute attr) {
    if(GenService.isGemList(attr)){
      getCD4C().addMethod(deSer, "umlp.deser.deserializeGemListField", cdName, originalClassName, attr.getName(), attr.printType());
    } else if(GenService.isGem(attr)) {
      ASTMCBasicGenericType gemType = (ASTMCBasicGenericType) attr.getMCType();
      String innerType = new CD4CodeFullPrettyPrinter().prettyprint(gemType.getMCTypeArgument(0).getMCTypeOpt().get());
      getCD4C().addMethod(
          deSer,
          "umlp.deser.deserializeGemField",
          cdName,
          originalClassName,
          attr.getName(),
          attr.printType(),
          innerType
      );
    } else if(GenService.isList(attr)) {
      // TODO
    } else if(GenService.isOptional(attr)) {
      // TODO
    } else {
      String type = attr.printType().replaceAll("^java[.]lang[.]", "");
      // TODO: Other simple types!
      // Alternative: All other types are Gem?
      if(isSimpleType(type)) {
        getCD4C().addMethod(deSer,
            "umlp.deser.deserializeSimpleField",
            originalClassName,
            attr.getName(),
            type
        );
      }
    }
  }

  private static boolean isSimpleType(String type) {
    type = type.replaceAll("^java[.]lang[.]", "");
    return List.of("string", "int", "integer", "boolean", "float", "double").contains(type.toLowerCase());
  }

  @Override
  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.*");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.*");
    getCD4C().addImport(clazz, "umlp.rte.*");
  }
}

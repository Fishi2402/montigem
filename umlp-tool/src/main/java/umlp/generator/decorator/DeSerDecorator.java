package umlp.generator.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4CTemplateHelper;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

public class DeSerDecorator extends DomainClassDecorator{
  public DeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
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
        .filter(attr -> isSimpleType(attr.printType()) || GenService.isGemList(attr) || GenService.isGem(attr))
        .collect(Collectors.toList());

    attrList
        .stream()
        .filter(attr -> !attr.getModifier().isDerived())
        // TODO: transient modifier?
        .forEach(attr -> {
          addSerMethod(originalClassName, deSer, attr);
          addDeserMethod(originalClassName, deSer, attr);
        });

    getCD4C().addMethod(deSer, "umlp.deser.serialize", originalClassName, attrList);
    getCD4C().addMethod(deSer, "umlp.deser.deserialize",
      cdName,
      originalClassName,
      attrList
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

  protected void addDeserMethod(String originalClassName, ASTCDClass deSer, ASTCDAttribute attr) {
    if(GenService.isGemList(attr)){
      getCD4C().addMethod(deSer, "umlp.deser.deserializeGemListField", originalClassName, attr.getName());
    } else if(GenService.isGem(attr)) {
      getCD4C().addMethod(deSer, "umlp.deser.deserializeGemField", originalClassName, attr.getName());
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
    return List.of("string", "int", "integer", "boolean", "float", "double").contains(type.toLowerCase());
  }

  @Override
  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.*");
    getCD4C().addImport(clazz, "de.monticore.symboltable.serialization.json.*");
  }
}

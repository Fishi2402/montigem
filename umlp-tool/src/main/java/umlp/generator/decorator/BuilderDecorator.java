/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.GenService.createInitialValue;

public class BuilderDecorator extends AbstractDecorator {

  public static final String BUILDER_SUFFIX = "Builder";

  public BuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    String className = domainClass.getName() + BUILDER_SUFFIX;

    ASTCDClass builderClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClass.getName()))
            .setName(className).build();

    // Constructors
    getCD4C().addConstructor(builderClass, "umlp.builder.Constructor", domainClass.getName());

    // Attributes
    List<ASTCDAttribute> attrList = Lists.newArrayList();
    attrList.addAll(getAttributesForFields(domainClass));
    attrList.addAll(getAttributesForAssociations(domainClass));
    builderClass.addAllCDMembers(attrList);

    /* Variante ohne CD4C
    builderClass.addCDMember(getCDAttributeFacade()
            .createAttribute(PROTECTED.build(),domainClass.getName()+"Validator", "validator)"));
    builderClass.addCDMember(getCDAttributeFacade()
            .createAttribute(PROTECTED.build(),"boolean", "buildFinished)"));
    */

    getCD4C().addAttribute(builderClass, "protected " + domainClass.getName() + "Validator validator;");
    getCD4C().addAttribute(builderClass, "protected boolean buildFinished = false");

    // Methods
    createGetterSetterMethods(domainClass, builderClass, domainClass.getCDAttributeList());
    getCD4C().addMethod(builderClass, "umlp.builder.Build", domainClass.getName(), name);
    getCD4C().addMethod(builderClass, "umlp.builder.IsFinished");
    getCD4C().addMethod(builderClass, "umlp.builder.GetValidatorMessages");
    getCD4C().addMethod(builderClass, "umlp.builder.IsBuildable");

    // Imports
    addImports(builderClass);

    return builderClass;
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();
    List<ASTCDAttribute> visibleAttributes = domainClass.getCDAttributeList().stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // get all visible parameters
    for (ASTCDAttribute visibleAttribute : visibleAttributes) {
      ASTMCPrimitiveType type = getMCTypeFacade().createBooleanType();
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
              type, "isPresent" + visibleAttribute.getName());
      attribute.setInitial(createInitialValue(type));
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass domainClass) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<ASTCDAssocSide> associations = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisible(a)).collect(Collectors.toList());

    // handle associations
    for (ASTCDAssocSide association : associations) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
              association.getSymbol().getType().print(), association.getName());
      attribute.setInitial(createInitialValue(association.getSymbol().getType()));
      attrList.add(attribute);
    }
    return attrList;
  }

  protected void createGetterSetterMethods(ASTCDClass domainClass, ASTCDClass builderClass,
                                           List<ASTCDAttribute> attrList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Attr",
              attribute.getName(), getCDPrinter().prettyprint(attribute.getMCType()), domainClass.getName() + BUILDER_SUFFIX);
    }
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, "umlp.rte.ValidationMessage");
  }

}

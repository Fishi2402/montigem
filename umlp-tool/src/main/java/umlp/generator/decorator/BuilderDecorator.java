/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.decorator.GenService.createInitialValue;
import static umlp.generator.decorator.GenService.createType;

public class BuilderDecorator extends AbstractDecorator {

  public static final String BUILDER_SUFFIX = "Builder";

  public BuilderDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    String name = origCD.getCDDefinition().getName();
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getModifier().isDerived())
      .forEach(c -> astcdPackage.addCDElement(createClass(c, name)));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, String name) {
    String className = domainClass.getName() + BUILDER_SUFFIX;

    ASTCDClass builderClass = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClass.getName()))
            .setName(className).build();

    // Constructors
    getCD4C().addConstructor(builderClass, "umlp.builder.Constructor", domainClass.getName());
    List<ASTCDAttribute> attrFields = CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisibleNotDerived(a)).collect(Collectors.toList());
    List<ASTCDAssocSide> attrAssocs = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
            .filter(a -> GenService.isVisibleNotDerived(a)).collect(Collectors.toList());
    // Attributes
    List<ASTCDAttribute> attrList = getAttributesForFields(domainClass, attrFields);
    attrList.addAll(getAttributesForAssociations(domainClass, attrAssocs));
    builderClass.addAllCDMembers(attrList);

    getCD4C().addAttribute(builderClass, "protected boolean buildFinished = false");

    // Methods
    createSetAndCheckMethods(domainClass, builderClass, attrFields);
    getCD4C().addMethod(builderClass, "umlp.builder.Build", domainClass.getName(), name);
    getCD4C().addMethod(builderClass, "umlp.builder.IsFinished");
    getCD4C().addMethod(builderClass, "umlp.builder.IsBuildable", attrList);

    // Imports
    addImports(builderClass);

    return builderClass;
  }

  protected List<ASTCDAttribute> getAttributesForFields(ASTCDClass domainClass, List<ASTCDAttribute> domainAttrList) {
    List<ASTCDAttribute> attributesList = Lists.newLinkedList();

    // get all visible parameters
    for (ASTCDAttribute visibleAttribute : domainAttrList) {
      ASTMCPrimitiveType type = getMCTypeFacade().createBooleanType();
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
              type,  "is" + StringTransformations.capitalize(visibleAttribute.getName()) + "Present");
      attribute.setInitial(createInitialValue(type));
      attributesList.add(attribute);
    }
    return attributesList;
  }

  protected List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass domainClass, List<ASTCDAssocSide> domainAssocList) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();

    // handle associations
    for (ASTCDAssocSide association : domainAssocList) {
      // TODO: Was muss hier gemacht werden
    }
    return attrList;
  }

  protected void createSetAndCheckMethods(ASTCDClass domainClass, ASTCDClass builderClass,
                                          List<ASTCDAttribute> attrList) {
    for (ASTCDAttribute attribute : attrList) {
      getCD4C().addMethod(builderClass, "umlp.builder.Attr",
              attribute.getName(), getCDPrinter().prettyprint(createType(attribute)), domainClass.getName() + BUILDER_SUFFIX);
      getCD4C().addMethod(builderClass, "umlp.builder.Check",
              attribute.getName(), domainClass.getName());
    }
  }

  protected void addImports(ASTCDType clazz) {
    getCD4C().addImport(clazz, "java.util.List");
    getCD4C().addImport(clazz, "java.util.Optional");
    getCD4C().addImport(clazz, "umlp.rte.NotificationScope");
  }

}

/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class DomainClassDecorator extends DomainTypeDecorator {

  protected final MCPath hwPath;

  public DomainClassDecorator(final GlobalExtensionManagement glex,
                              MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c)));

    origCD.getCDDefinition().getCDEnumsList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addCDElement(c.deepClone()));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    ASTCDInterfaceUsage implementsUsage = getInterfaceUsage(domainClass);
    ASTModifier modifier = domainClass.getModifier().isAbstract() ? PUBLIC_ABSTRACT.build() : PUBLIC.build();
    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
        .setModifier(modifier)
        .setCDInterfaceUsage(implementsUsage)
        .setName(domainClass.getName());
    if (domainClass.isPresentCDExtendUsage()) {
      builder.setCDExtendUsage(getExtendUsageFacade()
          .createCDExtendUsage(domainClass.getCDExtendUsage().getSuperclass(0).deepClone()));
    }
    ASTCDClass clazz = builder.build();
    // Constructors
    getCD4C().addConstructor(clazz, "umlp.domain.Constructor", domainClass.getName());

    if (!domainClass.isPresentCDExtendUsage()) {
      // Attribute gemId and getter and setter
      ASTCDAttribute gemAttr = getCD4C().addAttribute(clazz, "protected readonly long gemId = -32;");
      clazz.addAllCDMembers(new MethodDecorator(glex).decorate(gemAttr));
    }

    // Attribute observerList
    ASTCDAttribute attrObserver = getCD4C().addAttribute(clazz,
        " protected List<" + domainClass.getName() + "Observer> " + StringTransformations.uncapitalize(domainClass.getName()) + "Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint("= new ArrayList<>()"));
    glex.replaceTemplate("cd2java.Annotations", attrObserver,
        new StringHookPoint("transient"));

    // Attributes
    AttributesTuple attributes = getAllAttributes(domainClass, false);
    List<ASTCDAttribute> derivedAttribFields = filterDerivedAttributes(attributes.attrFields, true);

    // Add attributes to class
    clazz.addAllCDMembers(filterDerivedAttributes(attributes.attrList, false));

    // General methods
    createObserverMethods(domainClass, clazz);
    getCD4C().addMethod(clazz, "umlp.domain.Build", domainClass.getName());

    // Methods for class attributes
    createGetterSetterForAttrs(domainClass, clazz, filterDerivedAttributes(attributes.attrFields, false), false);
    createGetterSetterForAttrs(domainClass, clazz, derivedAttribFields, true);
    createCheckMethods(domainClass, clazz);

    // Stereotype
    if (derivedAttribFields.size() > 0) {
      String derivedAttribs = derivedAttribFields.stream().map(ASTCDAttributeTOP::getName).collect(Collectors.joining(", "));
      ASTStereoValue stereoValue = UMLPMill.stereoValueBuilder().setName("needsTOP").setContent(derivedAttribs).build();
      ASTStereotype stereotype = UMLPMill.stereotypeBuilder().addValues(stereoValue).build();
      modifier.setStereotype(stereotype);
    }

    // Methods for class assocs
    createGetterSetterForAssocs(clazz, attributes.attrAssocs, false);

    // Imports
    addImports(clazz);
    return clazz;
  }

  protected ASTCDInterfaceUsage getInterfaceUsage(ASTCDClass domainClass) {
    ASTCDInterfaceUsage interfaceUsage = getInterfaceUsageFacade()
        .createCDInterfaceUsage("GemIdentifiable", "GemBuildable");
    for (ASTMCObjectType superInterface : domainClass.getInterfaceList()) {
      interfaceUsage.addInterface(superInterface);
    }
    return interfaceUsage;
  }

  protected void createCheckMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    for (ASTCDAttribute attr : CDSymbolTables.getAttributesInHierarchy(domainClass)
        .stream().filter(GenService::isVisibleNotDerived).collect(Collectors.toList())) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
          domainClass.getName(), attr.getName(),
          !GenService.isList(attr) && !GenService.isOptional(attr));
    }
    for (ASTCDAssocSide assoc : CDSymbolTables.getAssociationsInHierarchy(domainClass)) {
      getCD4C().addMethod(clazz, "umlp.domain.Check",
          domainClass.getName(), assoc.getName(),
          !GenService.isOptional(assoc));
    }
  }

  protected void createObserverMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.domain.AddObserver", domainClass.getName(), clazz.isPresentCDExtendUsage());
    getCD4C().addMethod(clazz, "umlp.domain.RemoveObserver", domainClass.getName());
    getCD4C().addMethod(clazz, "umlp.domain.ContainsObserver", domainClass.getName());
  }

  /**
   * Filters the derived attributes from the list of attributes
   *
   * @param attributes list of attributes
   * @param getDerived whether to return derived attributes or non-derived attributes
   * @return list of attributes (only derived ones if getDerived is true else only non-derived ones)
   */
  protected List<ASTCDAttribute> filterDerivedAttributes(List<ASTCDAttribute> attributes, boolean getDerived) {
    return attributes.stream().filter(attr -> !getDerived ^ attr.getModifier().isDerived()).collect(Collectors.toList());
  }
}

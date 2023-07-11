/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class DomainClassDecorator extends DomainTypeDecorator {

  // TODO: add/remove methods for lists of basic types, e.g. List<String>

  protected final MCPath hwPath;

  public DomainClassDecorator(final GlobalExtensionManagement glex,
                              MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(origCD, c))));

    origCD.getCDDefinition().getCDEnumsList().stream().filter(c -> !c.getSymbol().isIsDerived())
        .forEach(c -> {
          ASTCDEnum enumCopy = c.deepClone();
          enumCopy.setModifier(PUBLIC.build());
          astcdPackage.addCDElement(withOrigin(enumCopy));
        });
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

    // Stereotype
    List<ASTCDAttribute> derivedAttribFields = filterDerivedAttributes(domainClass.getCDAttributeList(), true);
    if (derivedAttribFields.size() > 0) {
      String derivedAttribs = derivedAttribFields.stream()
          .map(attr -> {
            SourcePosition sourcePosition = attr.get_SourcePositionStart();
            return String.format(
                "%s in %s:%s:%s",
                attr.getName(),
                sourcePosition.getFileName().orElse("unknown"),
                sourcePosition.getLine(),
                sourcePosition.getColumn()
            );
          }).collect(Collectors.joining(", "));
      ASTStereotype stereotype = UMLPMill.stereotypeBuilder()
          .addValues(
              TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(
                  String.format("Custom implementation for class: %s required. Contains derived attribute(s): %s", domainClass.getName(), derivedAttribs)
              )
          ).build();
      modifier.setStereotype(stereotype);
    }

    //Build class
    ASTCDClass clazz = builder.build();

    // Constructors
    getCD4C().addConstructor(clazz, getConstructorTemplate(), domainClass.getName());

    // Attributes
    AttributesTuple attributes = getAllAttributes(domainClass, false, origCD);

    if (!domainClass.isPresentCDExtendUsage()) {
      // Attribute gemId and getter and setter
      ASTCDAttribute gemAttr = getCD4C().addAttribute(clazz, "protected readonly long gemId = -32;");
      clazz.addAllCDMembers(new MethodDecorator(glex).decorate(gemAttr));
    }

    // Attribute observerList
    ASTCDAttribute attrObserver = getCD4C().addAttribute(clazz,
        " protected List<" + domainClass.getName() + "Observer> " + StringTransformations.uncapitalize(domainClass.getName()) + "Observers");
    glex.replaceTemplate("cd2java.Value", attrObserver, new StringHookPoint(defaultListValue()));
    addTransientModifier(attrObserver);

    // Add attributes to class
    List<ASTCDAttribute> nonDerivedAttrs = filterDerivedAttributes(attributes.attrList, false);
    clazz.addAllCDMembers(nonDerivedAttrs);
    for (ASTCDAttribute attr : nonDerivedAttrs) {
      if (GenService.isGem(attr) && !GenService.isGemList(attr)) {
        glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(getInitialGemValue()));
      }
    }

    // General methods
    createObserverMethods(domainClass, clazz);
    createEqualsMethod(domainClass, clazz);
    getCD4C().addMethod(clazz, getBuildTemplate(), domainClass.getName());
    createIsBuildFinishedMethod(clazz);

    // Methods for class attributes
    createGetterSetterForAttrs(domainClass, clazz, filterDerivedAttributes(attributes.attrFields, false), false);
    createGetterSetterForAttrs(domainClass, clazz, derivedAttribFields, true);
    createCheckMethods(domainClass, clazz);

    // Methods for class assocs
    createGetterSetterForAssocs(clazz, attributes.attrAssocs, false);

    // Imports
    addImports(domainClass, clazz);
    return clazz;
  }

  protected String getInitialGemValue() {
    return "= new Gem<>()";
  }

  protected void createIsBuildFinishedMethod(ASTCDClass clazz) {
  }

  protected String getConstructorTemplate() {
    return "umlp.domain.Constructor";
  }

  protected String getBuildTemplate() {
    return "umlp.domain.Build";
  }

  protected void addTransientModifier(ASTCDAttribute attrObserver) {
    glex.replaceTemplate("cd2java.Annotations", attrObserver,
        new StringHookPoint("transient"));
  }

  protected String defaultListValue() {
    return "= new ArrayList<>()";
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
          !GenService.isOptional(assoc), GenService.isList(assoc));
    }
  }

  protected void createObserverMethods(ASTCDClass domainClass, ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.domain.AddObserver", domainClass.getName(), clazz.isPresentCDExtendUsage());
    getCD4C().addMethod(clazz, "umlp.domain.RemoveObserver", domainClass.getName());
    getCD4C().addMethod(clazz, "umlp.domain.ContainsObserver", domainClass.getName());
  }

  protected void createEqualsMethod(ASTCDClass domainClass, ASTCDClass clazz) {

    List<String> primitives = new ArrayList<>();
    List<String> primitiveBooleans = new ArrayList<>();
    List<String> wrappedBooleans = new ArrayList<>();
    List<String> objects = new ArrayList<>();
    FullSynthesizeFromCD4Code synthesize = new FullSynthesizeFromCD4Code();

    for (ASTCDAttribute attribute : domainClass.getCDAttributeList()) {
      if (attribute.getModifier().isDerived() || attribute.getModifier().isAbstract()) {
        continue;
      }
      TypeCheckResult result = synthesize.synthesizeType(attribute.getMCType());
      if (result.isPresentResult()) {
        SymTypeExpression expression = result.getResult();
        boolean isBool = TypeCheck.isBoolean(expression);
        if (expression.isPrimitive()) {
          if (isBool) {
            primitiveBooleans.add(attribute.getName());
          } else {
            primitives.add(attribute.getName());
          }
        } else {
          if (isBool) {
            wrappedBooleans.add(attribute.getName());
          } else {
            objects.add(attribute.getName());
          }
        }
      }
    }

    getCD4C().addMethod(clazz, getEqualsTemplate(), primitives, objects, primitiveBooleans, wrappedBooleans, domainClass.getName());
  }

  protected String getEqualsTemplate() {
    return "umlp.domain.Equals";
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

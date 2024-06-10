/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotypeBuilder;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.commands.OwnCommands;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;

public class DomainClassDecorator extends DomainTypeDecorator implements OwnCommands {

  // TODO: add/remove methods for lists of basic types, e.g. List<String>

  protected final MCPath hwPath;

  public DomainClassDecorator(final GlobalExtensionManagement glex,
                              MCPath hwPath) {
    super(glex);
    this.hwPath = hwPath;
  }

  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    getNonDerivedDomainClasses(origCD).forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(
        origCD,
        c))));

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
    ASTStereotypeBuilder stereotype = UMLPMill.stereotypeBuilder();
    List<ASTCDAttribute> derivedAttribFields = filterDerivedAttributes(domainClass.getCDAttributeList(), true);
    if (!derivedAttribFields.isEmpty()) {
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
      stereotype.addValues(
          TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(
              String.format("Custom implementation for class: %s required - use the TOP mechanism. Contains derived attribute(s): %s", "%s", derivedAttribs)
          )
      );
    }
    List<ASTCDAssocSide> derivedAssocs = CDSymbolTables.getAssociationsInHierarchy(domainClass).stream()
        .filter(GenService::isSymbolDerived)
        .collect(Collectors.toList());
    if (!derivedAssocs.isEmpty()) {
      String derivedAssocsStr = derivedAssocs.stream()
          .map(assoc -> {
            SourcePosition sourcePosition = assoc.get_SourcePositionStart();
            return String.format(
                "%s in %s:%s:%s",
                assoc.getName(),
                sourcePosition.getFileName().orElse("unknown"),
                sourcePosition.getLine(),
                sourcePosition.getColumn()
            );
          }).collect(Collectors.joining(", "));
      stereotype.addValues(
          TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(
              String.format("Custom implementation for class: %s required - use the TOP mechanism. Contains derived association(s): %s", "%s", derivedAssocsStr)
          )
      );
    }
    modifier.setStereotype(stereotype.build());
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
    ASTCDAttribute attrObserver = addAttribute(clazz,
                                               " protected List<" + domainClass.getName() + "Observer> " + StringTransformations.uncapitalize(
                                                   domainClass.getName()) + "Observers",
                                               defaultListValue());
    addTransientModifier(attrObserver);

    // Add attributes to class
    List<ASTCDAttribute> nonDerivedAttrs = filterDerivedAttributes(attributes.attrList, false);
    clazz.addAllCDMembers(nonDerivedAttrs);
    for (ASTCDAttribute attr : nonDerivedAttrs) {
      if (GenService.isGem(attr) && !GenService.isGemList(attr)) {
        glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(getInitialGemValue()));
      } else if (GenService.isList(attr)) {
        glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(defaultListValue()));
      } else if (GenService.isBoolean(attr.getMCType())) {
        glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(defaultBooleanValue()));
      }
    }

    // General methods
    createObserverMethods(domainClass, clazz);
    createEqualsMethod(domainClass, clazz);
    getCD4C().addMethod(clazz, getBuildTemplate(), domainClass.getName());
    createIsBuildFinishedMethod(clazz);
    createIsBuildableMethod(clazz);

    // Methods for class attributes
    createGetterSetterForAttrs(domainClass, clazz, filterDerivedAttributes(attributes.attrFields, false), false);
    createGetterSetterForAttrs(domainClass, clazz, derivedAttribFields, true);
    createCheckMethods(domainClass, clazz);

    // Methods for class assocs
    createGetterSetterForAssocs(domainClass, clazz, attributes.attrAssocs, false);

    if (TagLookUp.isSingleton(domainClass)) {
      domainClass.getCDMethodList().forEach(m -> createExeceuteCommandMethod(domainClass, clazz, m));
    }

    // Imports
    addImports(domainClass, clazz);
    addExtendedImport(origCD, clazz);
    addImportsForCommands(domainClass, clazz);
    return clazz;
  }

  protected void addExtendedImport(ASTCDCompilationUnit origCD, ASTCDType ast) {
    if (!origCD.getCDDefinition().getModifier().isPresentStereotype()) {
      return;
    }
    List<ASTStereoValue> valueList = origCD.getCDDefinition().getModifier().getStereotype().getValuesList();
    List<String> extendedCDs = valueList.stream().filter(value -> value.getName().equals("extends"))
        .map(ASTStereoValue::getValue).collect(Collectors.toList());

    extendedCDs.forEach(ecd -> {
      Optional<DiagramSymbol> diagramSymbol = origCD.getEnclosingScope().resolveDiagram(ecd);
      if (diagramSymbol.isPresent()) {
        String packageName = diagramSymbol.get().getPackageName().isEmpty()?diagramSymbol.get().getName():Joiners.DOT.join(diagramSymbol.get().getPackageName(), diagramSymbol.get().getName());
        String importName = Joiners.DOT.join(packageName.toLowerCase(), diagramSymbol.get().getName()+ManagerDecorator.SUFFIX);
        getCD4C().addImport(ast, importName);
      }
    });
  }
  protected String getInitialGemValue() {
    return "= new Gem<>()";
  }

  protected void createIsBuildFinishedMethod(ASTCDClass clazz) {
  }

  protected void createIsBuildableMethod(ASTCDClass clazz) {
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

  protected String defaultBooleanValue() {
    return "= false";
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
          !GenService.isList(attr) && !GenService.isOptional(attr) && !attr.getModifier().isDerived(),
          false
      );
    }
    for (ASTCDAssocSide assoc : CDSymbolTables.getAssociationsInHierarchy(domainClass)) {
      if (GenService.isAStLeastOne(assoc)) {
        getCD4C().addMethod(clazz, "umlp.domain.CheckCard",
            domainClass.getName(),
            assoc.getName(),
            !GenService.isSymbolDerived(assoc),
            GenService.isList(assoc)
        );
      } else {
        getCD4C().addMethod(clazz, "umlp.domain.Check",
            domainClass.getName(),
            assoc.getName(),
            !GenService.isOptional(assoc) && !GenService.isSymbolDerived(assoc),
            GenService.isList(assoc)
        );
      }
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

    for (ASTCDAttribute attribute : CDSymbolTables.getAttributesInHierarchy(domainClass)) {
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
    addDeepEqualsMethod(domainClass, clazz);
  }

  protected String getEqualsTemplate() {
    return "umlp.domain.Equals";
  }

  protected void createExeceuteCommandMethod(ASTCDClass domainClass, ASTCDClass clazz, ASTCDMethod command) {
    List<ASTCDParameter> adjustedParameters = Lists.newArrayList();
    command.getCDParameterList().forEach(p -> adjustedParameters.add(getAdjustedMethodParameter(p)));
    String params = Joiners.COMMA.join(adjustedParameters.stream().map(p ->
            UMLPMill.prettyPrint(p.getMCType(), false) + " " + p.getName()).collect(Collectors.toList()));
    String call = Joiners.COMMA.join(adjustedParameters.stream().map(p -> p.getName()).collect(Collectors.toList()));
    String returnType = buildReturnType(command, clazz);
    String asType = returnType.contains("<")? returnType.substring(0, returnType.indexOf("<")): returnType;
    getCD4C().addMethod(clazz, getExecuteCommandTemplate(), StringTransformations.capitalize(command.getName()),
        params, returnType, call, asType);
  }

  protected void addImportsForCommands(ASTCDClass domainType, ASTCDType cdType) {
    if (TagLookUp.isSingleton(domainType) && !domainType.getCDMethodList().isEmpty()) {
      getCD4C().addImport(cdType, PathUtil.getBackendRTECommandPath("CommandManager"));
      domainType.getCDMethodList().forEach(m -> getCD4C().addImport(cdType,
          getPackageName(UMLPSymbolTables.getDiagramFor(domainType), "commands.") +
          StringTransformations.capitalize(m.getName())));
    }
  }

  protected String getExecuteCommandTemplate() {
    return "umlp.domain.ExecuteCommand";
  }

  protected ASTCDParameter getAdjustedMethodParameter(ASTCDParameter param) {
    ASTCDParameter adjustedParam = param.deepClone();
    if (isGem(param.getSymbol().getType())) {
      adjustedParam.setName(param.getName() + "GemId");
      adjustedParam.setMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder()
                                  .setPrimitive(ASTConstantsMCBasicTypes.LONG).build());
    }
    if (isGemList(param.getSymbol().getType())) {
      adjustedParam.setName(param.getName() + "GemId");
      adjustedParam.setMCType(MCTypeFacade.getInstance().createListTypeOf("Long"));
    }
    return adjustedParam;
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

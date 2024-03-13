/* (c) https://github.com/MontiCore/monticore */
package umlp.generator;

import de.monticore.ast.ASTNode;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.facade.CDStereotypeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheck;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereoValueTOP;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.util.UMLPSymbolTables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO refactor this class
public abstract class AbstractDecorator {

  protected static Map<ASTNode, String> origins = new LinkedHashMap<>();

  protected static HookPoint originHookPoint = new OriginHookPoint(origins);

  public static HookPoint getOriginHookPoint() {
    return originHookPoint;
  }


  public AbstractDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public static final String DEFAULT_PACKAGE = "";

  public static final String COMMAND_PACKAGE = DEFAULT_PACKAGE + "commands";

  public static final String GEMLIST_PACKAGE = DEFAULT_PACKAGE + "gemlists";

  public static final String CLIENT_PACKAGE = DEFAULT_PACKAGE + "client";

  public static final String SHELLAPI_PACKAGE = DEFAULT_PACKAGE + "shellapi";

  public static boolean hasOCL = false;

  protected final GlobalExtensionManagement glex;

  protected final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter(), false);

  protected TypeCheck typeCheck;

  protected GlobalExtensionManagement getGlex() {
    return glex;
  }

  protected MCTypeFacade getMCTypeFacade() {
    return MCTypeFacade.getInstance();
  }

  protected CDAttributeFacade getCDAttributeFacade() {
    return CDAttributeFacade.getInstance();
  }

  protected CDConstructorFacade getCDConstructorFacade() {
    return CDConstructorFacade.getInstance();
  }

  protected CDMethodFacade getCDMethodFacade() {
    return CDMethodFacade.getInstance();
  }

  protected CDParameterFacade getCDParameterFacade() {
    return CDParameterFacade.getInstance();
  }

  protected CDExtendUsageFacade getExtendUsageFacade() {
    return CDExtendUsageFacade.getInstance();
  }

  protected CDInterfaceUsageFacade getInterfaceUsageFacade() {
    return CDInterfaceUsageFacade.getInstance();
  }

  protected CDStereotypeFacade getStereotypeFacade() {
    return CDStereotypeFacade.getInstance();
  }

  protected CD4C getCD4C() {
    return CD4C.getInstance();
  }

  protected CD4CodeFullPrettyPrinter getCDPrinter() {
    return printer;
  }

  public TypeCheck getTypeCheck() {
    return typeCheck;
  }

  public void setTypeCheck(TypeCheck typeCheck) {
    this.typeCheck = typeCheck;
  }

  /**
   * Adds an attribute with an initial value to the given {@link ASTCDType} without adding getters nor setters.
   *
   * @param target       Where to define the attribute
   * @param declaration  The declaration code of the attribute
   * @param initialValue The initial value code of the attribute. If blank no initial value will be added
   * @return The added {@link ASTCDAttribute}
   * @see #addAttribute(ASTCDType, boolean, boolean, String, String)
   */
  protected ASTCDAttribute addAttribute(ASTCDType target, String declaration, String initialValue) {
    return addAttribute(target, false, false, declaration, initialValue);
  }

  /**
   * Adds an attribute with an initial value to the given {@link ASTCDType}.
   *
   * @param target       Where to define the attribute
   * @param addGetter    Whether to add a getter
   * @param addSetter    Whether to add a setter
   * @param declaration  The declaration code of the attribute
   * @param initialValue The initial value code of the attribute. If blank no initial value will be
   *                     added. May start with an equals sign, but is not required to.
   * @return The added {@link ASTCDAttribute}
   * @see CD4C#addAttribute(ASTCDType, boolean, boolean, String)
   */
  protected ASTCDAttribute addAttribute(ASTCDType target, boolean addGetter, boolean addSetter, String declaration, String initialValue) {
    return addAttribute(target,
                        addGetter,
                        addSetter,
                        declaration,
                        Optional.of(initialValue)
                                .filter(s -> !s.isBlank())
                                .map(s -> (s.trim().startsWith("=") ? "" : " = ") + s)
                                .map(StringHookPoint::new));
  }

  /**
   * Adds an attribute with an initial value to the given {@link ASTCDType}.
   *
   * @param target                Where to define the attribute
   * @param addGetter             Whether to add a getter
   * @param addSetter             Whether to add a setter
   * @param declaration           The declaration code of the attribute
   * @param initialValueHookPoint An optional {@link HookPoint} with the initial value of the
   *                              attribute. The value must start with an equals sign!
   *                              (evaluate to an assign expression)
   * @return The added {@link ASTCDAttribute}
   * @see CD4C#addAttribute(ASTCDType, boolean, boolean, String)
   */
  protected ASTCDAttribute addAttribute(ASTCDType target, boolean addGetter, boolean addSetter, String declaration, Optional<HookPoint> initialValueHookPoint) {
    ASTCDAttribute attribute = getCD4C().addAttribute(target, addGetter, addSetter, declaration);

    initialValueHookPoint.ifPresent(hookPoint -> getGlex().replaceTemplate("cd2java.Value",
                                                                           attribute,
                                                                           hookPoint));

    return attribute;
  }

  /**
   * Creates a deep clone of the provided {@link ASTCDMethodSignature}, replaces its body with a
   * {@link HookPoint} and adds the created signature to the target {@link ASTCDClass}
   *
   * @param original         The original {@link ASTCDMethodSignature}
   * @param newBodyHookPoint A {@link HookPoint} to replace the body
   * @param target           A {@link ASTCDClass} to add the new method to
   * @return The new {@link ASTCDMethodSignature} with the new body
   * @see #replaceMethodBody(ASTCDMethodSignature, HookPoint)
   */
  protected ASTCDMethodSignature replaceMethodBody(ASTCDMethodSignature original, HookPoint newBodyHookPoint, ASTCDClass target) {
    ASTCDMethodSignature copy = replaceMethodBody(original, newBodyHookPoint);
    target.addCDMember(copy);
    return copy;
  }

  /**
   * Creates a deep clone of the provided {@link ASTCDMethodSignature} and replaces its body with a
   * {@link HookPoint}
   *
   * @param original         The original {@link ASTCDMethodSignature}
   * @param newBodyHookPoint A {@link HookPoint} to replace the body
   * @return The new {@link ASTCDMethodSignature} with the new body
   */
  protected ASTCDMethodSignature replaceMethodBody(ASTCDMethodSignature original, HookPoint newBodyHookPoint) {
    ASTCDMethodSignature copy = original.deepClone();
    getGlex().replaceTemplate(getCD4C().getEmptyBodyTemplate(), copy, newBodyHookPoint);
    return copy;
  }

  /**
   * Gets all {@link ASTCDClass}es of a class diagramm which are not derived nor abstract
   *
   * @param cd A class diagramm
   * @return A list of all concrete domain classes
   * @see #getNonDerivedDomainClasses(ASTCDCompilationUnit)
   */
  protected List<ASTCDClass> getConcreteDomainClasses(ASTCDCompilationUnit cd) {
    return getDomainClasses(cd, Filter.DERIVED.or(Filter.ABSTRACT).negate());
  }

  /**
   * Gets all {@link ASTCDClass}es of a class diagramm which are derived
   *
   * @param cd A class diagramm
   * @return A list of all derived domain classes
   * @see #getNonDerivedDomainClasses(ASTCDCompilationUnit)
   */
  protected List<ASTCDClass> getDerivedDomainClasses(ASTCDCompilationUnit cd) {
    return getDomainClasses(cd, Filter.DERIVED);
  }

  /**
   * Gets all {@link ASTCDClass}es of a class diagramm which are not derived (but may be abstract!)
   *
   * @param cd A class diagramm
   * @return A list of all non-derived domain classes
   * @see #getConcreteDomainClasses(ASTCDCompilationUnit)
   */
  protected List<ASTCDClass> getNonDerivedDomainClasses(ASTCDCompilationUnit cd) {
    return getDomainClasses(cd, Filter.DERIVED.negate());
  }

  /**
   * Gets all classes from the specified class diagramm which match the given filter. Best used in
   * combination with {@link Filter}
   *
   * @param cd     A class diagramm
   * @param filter A filter
   * @return A list with all matching classes
   * @see Filter
   */
  protected List<ASTCDClass> getDomainClasses(ASTCDCompilationUnit cd, Predicate<Filter.Wrapper> filter) {
    return cd.getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(c -> filter.test(Filter.Wrapper.wrap(c)))
        .collect(Collectors.toList());
  }

  /**
   * Gets all {@link ASTCDInterface}s of a class diagramm which are not derived
   *
   * @param cd A class diagramm
   * @return A list of all non-derived domain interfaces
   */
  protected List<ASTCDInterface> getNonDerivedDomainInterfaces(ASTCDCompilationUnit cd) {
    return getDomainInterfaces(cd, Filter.DERIVED.negate());
  }

  /**
   * Gets all interfaces from the specified class diagramm which match the given filter. Best used in
   * combination with {@link Filter}
   *
   * @param cd     A class diagramm
   * @param filter A filter
   * @return A list with all matching interfaces
   * @see Filter
   */
  protected List<ASTCDInterface> getDomainInterfaces(ASTCDCompilationUnit cd, Predicate<Filter.Wrapper> filter) {
    return cd.getCDDefinition()
        .getCDInterfacesList()
        .stream()
        .filter(c -> filter.test(Filter.Wrapper.wrap(c)))
        .collect(Collectors.toList());
  }

  protected ASTCDPackage getPackage(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD, String packageName) {
    String completeName = getPackageName(origCD, packageName);
    Optional<ASTCDPackage> astCDPackage = decoratedCD.getCDDefinition().getCDPackagesList().stream()
        .filter(p -> p.getName().equals(completeName)).findFirst();
    if (astCDPackage.isPresent()) {
      return astCDPackage.get();
    } else {
      ASTCDPackage p = CD4CodeMill.cDPackageBuilder()
          .setMCQualifiedName(getMCTypeFacade().createQualifiedName(completeName)).build();
      decoratedCD.getCDDefinition().addCDElement(p);
      return p;
    }
  }

  protected String getPackageName(ASTCDCompilationUnit origCD, String subPackage) {
    String origPackage = Names.constructQualifiedName(origCD.getCDPackageList(), origCD.getCDDefinition().getName());
    return (subPackage.isEmpty() ? origPackage : Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

  protected String getPackageName(DiagramSymbol cD, String subPackage) {
    String origPackage = cD.getPackageName().isEmpty()? cD.getName().toLowerCase() :
        Joiners.DOT.join(cD.getPackageName(), cD.getName().toLowerCase());
    return (subPackage.isEmpty() ? origPackage : Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

  protected Optional<String> getPackageFromFQNString(String fQN) {
    String[] subNames = fQN.split("\\.");
    if (subNames.length > 1) {
      String[] packageNamesOnly = Arrays.copyOf(subNames, subNames.length - 1);
      return Optional.of(Joiners.DOT.join(packageNamesOnly));
    } else {
      return Optional.empty();
    }
  }

  protected String getTypeFromFQNString(String fQN) {
    String[] subNames = fQN.split("\\.");
    return subNames[subNames.length - 1];
  }

  protected String getAssocName(ASTCDType type, ASTCDAssocSide assoc) {
    return type.getName()
        + "_"
        + StringTransformations.capitalize(assoc.getName());
  }

  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    CDSymbolTables.getAttributesInHierarchy(domainType).forEach(a -> {
      if (GenService.isJavaType(a.getSymbol().getType())) {
        getCD4C().addImport(cdType, a.getSymbol().getType().printFullName());
      } else if (a.getSymbol().getType().isGenericType()) {
        if (GenService.isJavaType(((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0))) {
          String imp = ((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0).printFullName();
          getCD4C().addImport(cdType, imp);
        }
      }
    });
    CDSymbolTables.getAssociationsInHierarchy(domainType).forEach(assocSide -> {
      getPackageFromFQNString(assocSide.getSymbol().getType().printFullName())
          .ifPresent(cdPackage ->
              getCD4C().addImport(cdType, Joiners.DOT.join(cdPackage.toLowerCase(),
                  getTypeFromFQNString(assocSide.getSymbol().getType().printFullName()))));
    });
    domainType.getInterfaceList().forEach(cdInterface ->
        cdInterface.getDefiningSymbol().ifPresent(definingSymbol ->
            getCD4C().addImport(cdType, Joiners.DOT.join(definingSymbol.getPackageName().toLowerCase(),
                definingSymbol.getName()))));
    domainType.getSuperclassList().forEach(cdExtension ->
        cdExtension.getDefiningSymbol().ifPresent(definingSymbol ->
            getCD4C().addImport(cdType, Joiners.DOT.join(definingSymbol.getPackageName().toLowerCase(),
                definingSymbol.getName()))));
  }

  protected void addDecoratorComment(ASTCDElement... nodes) {
    addDecoratorComment(Arrays.stream(nodes).collect(Collectors.toList()));
  }

  protected void addDecoratorComment(Collection<? extends ASTCDElement> nodes) {
    for (ASTCDElement node : nodes) {
      origins.put(node, "/* Created by decorator " + getClass().getName() + " */\n");
    }
  }

  protected ASTCDElement withOrigin(ASTCDElement element) {
    addDecoratorComment(element);
    return element;
  }

  protected Collection<? extends ASTCDElement> withOrigin(Collection<? extends ASTCDElement> elements) {
    addDecoratorComment(elements);
    return elements;
  }

  abstract public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD);


  protected String getCorrectManager(ASTCDAssociation assoc) {
    return UMLPSymbolTables.getDiagramFor(assoc).getCDDefinition().getName() + ManagerDecorator.SUFFIX;
  }

  protected String getCorrectManager(ASTCDType type) {
    return UMLPSymbolTables.getDiagramFor(type).getCDDefinition().getName() + ManagerDecorator.SUFFIX;
  }

  protected String getCorrectManager(ASTCDAssocSide assocSide) {
    return UMLPSymbolTables.getDiagramFor(assocSide.getSymbol().getType().getTypeInfo().getAstNode()).getCDDefinition().getName() + ManagerDecorator.SUFFIX;
  }

  protected String getCorrectManagerOfTargetType(ASTCDAssocSide assocSideTarget) {
    return getCorrectManagerFromSymTypeExpression(assocSideTarget.getSymbol().getType());
  }

  protected String getCorrectManager(ASTCDAttribute attribute) {
    return attribute.getSymbol().getPackageName() + ManagerDecorator.SUFFIX;
  }

  protected void addCorrectMangerImportForTargetType(ASTCDDefinition origCD, ASTCDType ast, SymTypeExpression type) {
    addImportForCorrectedPackage(origCD, ast, type, ManagerDecorator.SUFFIX);
  }

  protected void addImportForCorrectedPackage(ASTCDDefinition origCD, ASTCDType ast, SymTypeExpression type, String suffix){
    String[] fullName = type.printFullName().split("\\.");
    if (fullName.length <= 1) {
      return;
    }
    String potentialExtendedCDName = fullName[fullName.length - 2];
    String packageName = Joiners.DOT.join(Arrays.copyOfRange(fullName, 0, fullName.length - 1)).toLowerCase();
    // this checks if a cd import requires a manager
    if (!origCD.getModifier().isPresentStereotype()) {
      return;
    }
    List<ASTStereoValue> valueList = origCD.getModifier().getStereotype().getValuesList();
    List<String> extendedCDs = valueList.stream().filter(value -> value.getName().equals("extends"))
        .map(ASTStereoValueTOP::getValue).collect(Collectors.toList());

    // This checks a CD 'extends' another one, i.e. a UMLP Project extends/ is composed of another one
    if (extendedCDs.stream().anyMatch(cdName -> cdName.equals(potentialExtendedCDName))) {
      getCD4C().addImport(ast, Joiners.DOT.join(packageName, potentialExtendedCDName) + suffix);
    }
  }

  protected String getCorrectManagerFromSymTypeExpression(SymTypeExpression type) {
    String[] fullName = type.printFullName().split("\\.");
    if (fullName.length <= 1) {
      Log.error("Managername cannot be calculated. SymTypeExpression: '" + type + "'. Does not belong to a package defined by a Class Diagram.");
    }
    return fullName[fullName.length - 2] + ManagerDecorator.SUFFIX;
  }

  protected List<ASTCDCompilationUnit> getExtendedDefinitions(ASTCDCompilationUnit origCD){
    if(!origCD.getCDDefinition().getModifier().isPresentStereotype()){
      return List.of();
    }

    List<ASTStereoValue> valuesList = origCD.getCDDefinition().getModifier().getStereotype().getValuesList();
    List<String> extendedCDs = valuesList.stream().filter(value -> value.getName().equals("extends"))
        .map(ASTStereoValue::getValue).collect(Collectors.toList());
    List<ASTCDCompilationUnit> res = new ArrayList<>();

    extendedCDs.forEach(ecd -> {
      Optional<DiagramSymbol> diagramSymbol = origCD.getEnclosingScope().resolveDiagram(ecd);
      if (diagramSymbol.isPresent()) {
        ASTCDDefinition astNode = (ASTCDDefinition) diagramSymbol.get().getAstNode();
        res.add(UMLPSymbolTables.getDiagramFor(astNode));
      }
    });

    return res;
  }
}

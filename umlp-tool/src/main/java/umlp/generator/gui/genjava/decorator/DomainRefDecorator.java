/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class DomainRefDecorator extends GUIAbstractDecorator {

  protected final boolean isInputGui;

  protected ASTCDClass decoratedClass;

  protected final GUIComponentsVisitor viewComponent;

  public DomainRefDecorator(final GlobalExtensionManagement glex, boolean isInputGui, GUIComponentsVisitor viewComponent) {
    super(glex);
    this.isInputGui = isInputGui;
    this.viewComponent = viewComponent;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    // package
    String packageName = getPackageName(guiAst, DEFAULT_PACKAGE);
    ASTCDPackage astcdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
    decoratedAST.getCDDefinition().addCDElement(astcdPackage);

    // class
    decoratedClass = createClass(guiAst);
    addAdditionalAttributes(decoratedClass);
    astcdPackage.addCDElement(withOrigin(decoratedClass));

    // imports
    addImports(guiAst);

    //    Old GUI2JavaComponentsDecorator
    List<TemplateHookPoint> init = new ArrayList<>();
    for (ASTGUIComponent c : viewComponent.getComponents()) {
      GUI2JavaComponentDecorator decorator = instantiateComponentDecorator(guiAst);
      decorator.transform(decoratedClass, c);
      init.addAll(decorator.getInitHPs());
      if (util.isEventComponent(c)) {
        instantiateEventDecorator(guiAst).transform(decoratedClass, c);
      }
    }
    glex.replaceTemplate("umlp.gui2java.InitBody", init);
    for (ASTGUIGuard g : viewComponent.getGuards()) {
      GUI2JavaGuardDecorator decorator = instantiateGuardDecorator(guiAst);
      decorator.transform(decoratedClass, g);
    }

    // TODO AGe: rework iterate, not critical
    /*
    viewComponent.getIterates().stream()
        .forEach(c -> new IterateDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c));
    /**/
  }

  protected void addAdditionalAttributes(ASTCDClass decoratedClass) {
  }

  protected void addImports(ASTGUICompilationUnit guiAst) {
    boolean addedManager = false;
    for (ASTMCImportStatement i : guiAst.getMCImportStatementList()) {
      if (!isGUIModelImportInGUI(i)) {
        // add manager import once
        if (!addedManager) {
          addManagerImport(decoratedClass, util.gui2tsManagerImport(guiAst.getComponentName()));
          addedManager = true;
        }
        String qName = i.getQName();
        String importStr = Joiners.DOT.join(Names.getQualifier(qName).toLowerCase(), Names.getSimpleName(qName));
        // add builder import
        if (util.isCreateModel(guiAst.getComponentQName()) && util.isDomainImport(i)) {
          getCD4C().addImport(decoratedClass, importStr + BuilderDecorator.BUILDER_SUFFIX);
        } else if (util.isDomainImport(i)) {
          getCD4C().addImport(decoratedClass, importStr);
        } else if (!util.isBuiltIn(i)) {
          getCD4C().addImport(decoratedClass, qName);
        }
      }
    }

    addJavaImports();
  }

  protected void addJavaImports() {
    getCD4C().addImport(decoratedClass, "de.monticore.guidsl.types.GUIViewElement");
  }

  protected GUI2JavaComponentDecorator instantiateComponentDecorator(ASTGUICompilationUnit guiAst) {
    return new GUI2JavaComponentDecorator(glex, guiAst, viewComponent);
  }

  protected EventDecorator instantiateEventDecorator(ASTGUICompilationUnit guiAst) {
    return new EventDecorator(glex, guiAst, viewComponent);
  }

  protected GUI2JavaGuardDecorator instantiateGuardDecorator(ASTGUICompilationUnit guiAst) {
    return new GUI2JavaGuardDecorator(glex, guiAst);
  }

  protected void addManagerImport(ASTCDClass clazz, String manager) {
    getCD4C().addImport(clazz, manager);
  }

  protected String getBuildTemplate() {
    return "umlp.gui2java.Build";
  }

  protected ASTCDAttribute addBuilderForDomainMembers(ASTCDClass clazz, ASTGUIParam astguiParam) {
    // attr
    // --- replace this when TypeCheck can be used
    TypeSymbol typeInfo = astguiParam.getSymbol().getType().getTypeInfo();

    String attributeType = BuilderDecorator.getClassName(typeInfo.getName());
    String attributeName = astguiParam.getName();
    // --- end

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(
        CDModifier.PUBLIC.build(), attributeType, util.gui2cdAttrName(attributeName));

    clazz.addCDMember(domainAttribute);

    // gettter
    String builderGetter = util.gui2cdAttrGetterName(astguiParam.getName());
    ASTCDMethodSignature method = getCD4C().addMethod(clazz, getGetterParamTemplate(),
        builderGetter, attributeType, attributeName);
    addAnnotation(clazz, method);

    return domainAttribute;
  }

  protected String getGetterParamTemplate() {
    return "umlp.gui2java.GetParam";
  }

  protected ASTCDClass createClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    // empty class
    String className = domainClass.getName();
    ASTCDClass clazz = UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .build();

    // constructor, attrs, getters
    if (!domainClass.getGUIParamList().isEmpty()) {
      addInitMethod(clazz);

      List<ASTGUIParam> domainParams = domainClass.getGUIParamList().stream()
          .filter(p -> util.isDomainType(p))
          .collect(Collectors.toList());
      List<ASTGUIParam> domainListParams = domainClass.getGUIParamList().stream()
          .filter(p -> util.isDomainListType(p))
          .collect(Collectors.toList());
      List<ASTCDAttribute> domainAttributes = new ArrayList<>();

      domainAttributes.addAll(addDomainListAttributes(domainListParams, clazz));

      // attrs, getters
      if (util.isCreateModel(ast.getComponentQName())) {
        // Add Builder attributes and getter for domain type parameters
        addBuilderAttributes(domainParams, clazz, domainAttributes);

        // Add constructor
        addBuilderConstructor(clazz, domainAttributes, ast.getComponentQName());
      }
      else {
        // Add attributes
        addAttributes(domainParams, clazz, domainAttributes);

        List<ASTGUIParam> otherParams = domainClass.getGUIParamList().stream()
            .filter(p -> !util.isDomainType(p) && !util.isDomainListType(p)).collect(Collectors.toList());
        List<ASTCDAttribute> otherAttributes = new ArrayList<>();
        for (ASTGUIParam p : otherParams) {
          ASTCDAttribute cdAttr = addAttributForGUIParameter(clazz, p, CDModifier.PUBLIC.build());
          addAnnotation(cdAttr);
          otherAttributes.add(cdAttr);
        }
        List<String> params = new ArrayList<>();
        domainClass.forEachGUIParams(p -> params.add(
            getParam(p.getSymbol().getType(), p.getName())
        ));

        addEditConstructor(clazz, params, domainAttributes, otherAttributes);
      }

      // TODO AGe: readd error observers when error concept is realized
      /*
      if (isInputGui) {
        List<ASTMCQualifiedType> observers = domainAttributes.stream()
            .map(a -> getMCTypeFacade().createQualifiedType(ErrorObserverDecorator.getClassName(
                a.getMCType().printType())))
            .collect(Collectors.toList());
        if (!observers.isEmpty()) {
          clazz.setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(Lists.newArrayList()));
          clazz.getCDInterfaceUsage().addAllInterface(observers);
        }

        domainParams.forEach(a -> getCD4C().addImport(clazz, ErrorObserverDecorator.getClassName(
            a.getSymbol().getType().getTypeInfo().getFullName())));
        domainParams.forEach(a -> getCD4C().addImport(clazz, ErrorCollectorDecorator.getClassName(
            a.getSymbol().getType().getTypeInfo().getFullName())));
      }
      /**/
    }
    else {
      addDefaultConstructor(clazz);
    }

    if (UMLPGeneratorConfig.getInstance().isUseJsweet()) {
      clazz.getCDConstructorList().stream().findFirst().ifPresent(c -> {
        glex.replaceTemplate("cd2java.Annotations", c,
            new StringHookPoint("@OnInit"));
        getCD4C().addImport(clazz, "umlp.jsweet.extension.annotation.OnInit");
      });
    }

    return clazz;
  }

  protected void addAttributes(List<ASTGUIParam> domainParams, ASTCDClass clazz,
      List<ASTCDAttribute> domainAttributes) {
    List<HookPoint> hps = new ArrayList<>();
    for (ASTGUIParam p : domainParams) {
      ASTCDAttribute cdAttr = addAttributForGUIParameter(clazz, p, CDModifier.PUBLIC.build());
      addAnnotation(cdAttr);
      domainAttributes.add(cdAttr);

      if (util.getMainDomainModelSimpleName().isPresent() && !util.isEnumType(p.getSymbol().getType())) {
        // TODO AGe: body of init method currently uses different hooks, improve
        hps.add(new TemplateHookPoint(
            getInitDomainTemplate(),
            p.getName(),
            p.getMCType().printType(),
            util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX));
      }
      else {
        Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
      }
    }
    // fill init
    glex.setBeforeTemplate("umlp.gui2java.InitBody", hps);
  }

  protected void addBuilderAttributes(List<ASTGUIParam> domainParams, ASTCDClass clazz,
      List<ASTCDAttribute> domainAttributes) {
    List<String> builders = new ArrayList<>();
    List<HookPoint> hps = new ArrayList<>();
    for (ASTGUIParam p : domainParams) {
      ASTCDAttribute cdAttr = addBuilderForDomainMembers(clazz, p);
      addAnnotation(cdAttr);
      domainAttributes.add(cdAttr);

      String builder = p.getName();
      builders.add(builder);
      // fill init
      if (util.getMainDomainModelSimpleName().isPresent()) {
        // TODO AGe: body of init method currently uses different hooks, improve
        hps.add(new TemplateHookPoint(
            getInitBuilderTemplate(),
            builder,
            StringTransformations.uncapitalize(p.getMCType().printType()),
            util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX));
      }
      else {
        Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
      }
    }
    glex.setBeforeTemplate("umlp.gui2java.InitBody", hps);
    getCD4C().addMethod(clazz, getBuildTemplate(), builders);
  }

  protected List<ASTCDAttribute> addDomainListAttributes(List<ASTGUIParam> domainListParams, ASTCDClass clazz) {
    List<ASTCDAttribute> domainAttributes = new ArrayList<>();
    for (ASTGUIParam p : domainListParams) {
      ASTCDAttribute cdAttr = addAttributForGUIParameter(clazz, p, CDModifier.PUBLIC.build());
      addAnnotation(cdAttr);
      domainAttributes.add(cdAttr);

      if (util.getMainDomainModelSimpleName().isPresent()) {
        // TODO AGe: body of init method currently uses different hooks, improve
        glex.addAfterTemplate("umlp.gui2java.InitBody", new TemplateHookPoint(
            getInitDomainListTemplate(),
            p.getName(),
            util.getDomainListInnerType(p),
            util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX));
      }
      else {
        Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
      }
    }
    return domainAttributes;
  }

  protected void addDefaultConstructor(ASTCDClass clazz) {
    String className = clazz.getName();
    getCD4C().addConstructor(clazz, "umlp.domain.Constructor", className);
  }

  protected void addBuilderConstructor(
      ASTCDClass clazz, List<ASTCDAttribute> domainAttributes, String componentName
  ) {
    Optional<String> domainQName = util.getMainDomainModelSimpleName();
    if (domainQName.isPresent()) {
      String className = clazz.getName();
      getCD4C().addConstructor(clazz, getConstructorWithBuilderTemplate(),
          domainQName.get(), className, // TODO MB: Diagramm name UMLPDefSymbol
          domainAttributes, guiDSLPrinter);
    }
    else {
      Log.error("0x00000 GUI Model " + componentName
          + " is missing a connection to the domain model."
          + " Please specify the domain model using '-dm' flag.");
    }
  }

  protected void addEditConstructor(ASTCDClass clazz, List<String> params,
      List<ASTCDAttribute> domainAttributes, List<ASTCDAttribute> otherAttributes) {
    String className = clazz.getName();
    getCD4C().addConstructor(clazz, getConstructorTemplate(), className,
        Joiner.on(",").join(params), domainAttributes, otherAttributes,
        guiDSLPrinter, isInputGui);
  }

  protected String getInitDomainTemplate() {
    return "umlp.gui2java.InitDomain";
  }

  protected String getInitDomainListTemplate() {
    return "umlp.gui2java.InitDomainList";
  }

  protected void addAnnotation(ASTCDAttribute attr) {
  }

  protected void addInitMethod(ASTCDClass clazz) {
    ASTCDMethod method = this.getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), "init");
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getInitTemplate()));
    clazz.addCDMember(method);
  }

  protected String getInitTemplate() {
    return "umlp.gui2java.Init";
  }

  protected String getInitBuilderTemplate() {
    return "umlp.gui2java.InitBuilder";
  }

  protected String getConstructorWithBuilderTemplate() {
    return "umlp.gui2java.ConstructorWithBuilder";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2java.Constructor";
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam) {
    return addAttributForGUIParameter(clazz, astguiParam, CDModifier.PROTECTED.build());
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam, ASTModifier modifier) {
    // attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        modifier, transformTypeForAttr(clazz, astguiParam),
        util.gui2cdAttrName(astguiParam.getName())
    );
    addInitialValue(attr);
    clazz.addCDMember(attr);

    // getter
    String getter = util.gui2cdAttrGetterName(astguiParam.getName());
    ASTCDMethodSignature method = getCD4C().addMethod(clazz, getGetterParamTemplate(),
        getter, transformTypeForAttr(clazz, astguiParam).printType(), astguiParam.getName());
    addAnnotation(clazz, method);
    return attr;
  }

  private void addAnnotation(ASTCDClass clazz, ASTCDMethodSignature method) {
//    if (GenService.isList(((ASTCDMethod)method).getMCReturnType().getMCType())) {
//      glex.replaceTemplate("cd2java.Annotations", method, new StringHookPoint("@SynchronizedGemList"));
//      getCD4C().addImport(clazz, "umlp.jsweet.extension.annotation.SynchronizedGemList");
//    }
  }

  protected void addInitialValue(ASTCDAttribute attr) {
  }

  protected void addImportForGUIParameter(ASTCDClass clazz, String qName) {
    if (util.isBuiltIn(qName)) {
      return;
    }
    getCD4C().addImport(clazz, qName);
  }

  // TODO AGe: rewrite using ISymTypeVisitor?
  public Set<String> getImport(SymTypeExpression type) { //TODO GV: return List<String> for HashMap usw.
    Set<String> qNames = new HashSet<>();
    if (!type.isGenericType()) {
      qNames.add(type.getTypeInfo().getFullName());
    }
    else {
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics) type).getArgumentList();
      for (SymTypeExpression arg : arguments) {
        qNames.addAll(getImport(arg));
      }
    }
    return qNames;
  }

}

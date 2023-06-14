/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import com.google.common.base.Joiner;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.GUIAbstractDecorator;

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

  public DomainRefDecorator(final GlobalExtensionManagement glex, boolean isInputGui) {
    super(glex);
    this.isInputGui = isInputGui;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    // package
    String packageName = getPackageName(guiAst, DEFAULT_PACKAGE);
    ASTCDPackage astcdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
    decoratedAST.getCDDefinition().addCDElement(astcdPackage);

    // class
    decoratedClass = createClass(guiAst);
    astcdPackage.addCDElement(decoratedClass);

    // imports
    boolean addedManager = false;
    for (ASTMCImportStatement i : guiAst.getMCImportStatementList()) {
      if (!isGUIModelImportInGUI(i)) {
        // add manager import once
        if (!addedManager) {
          getCD4C().addImport(decoratedClass, util.gui2tsManagerImport(guiAst.getComponentName()));
          addedManager = true;
        }
        // add builder import
        if (util.isCreateModel(guiAst.getComponentQName()) && util.isDomainImport(i)) {
          getCD4C().addImport(decoratedClass, i.getQName() + BuilderDecorator.BUILDER_SUFFIX);
        }
        getCD4C().addImport(decoratedClass, i.getQName());
      }
    }
  }

  protected String getBuildTemplate() {
    return "umlp.gui2java.Build";
  }

  protected ASTCDAttribute addBuilderForDomainMembers(ASTCDClass clazz, ASTGUIParam astguiParam) {
    // attr
    // --- replace this when TypeCheck can be used
    TypeSymbol typeInfo = astguiParam.getSymbol().getType().getTypeInfo();

    String attributeType = BuilderDecorator.getClassName(typeInfo.getName());
    String attributeName = astguiParam.getName() + "Builder";
    // --- end

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(
        CDModifier.PUBLIC.build(), attributeType, util.gui2cdAttrName(attributeName));

    clazz.addCDMember(domainAttribute);

    // gettter
    String builderGetter = util.gui2cdAttrGetterName(astguiParam.getName());
    getCD4C().addMethod(clazz, getGetterParamTemplate(), builderGetter, attributeType, attributeName);

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
      ASTCDMethod init = addInitMethod();
      clazz.addCDMember(init);

      List<ASTGUIParam> domainParams = domainClass.getGUIParamList().stream()
          .filter(p -> util.isDomainType(p))
          .collect(Collectors.toList());
      List<ASTCDAttribute> domainAttributes = new ArrayList<>();
      // attrs, getters
      if (util.isCreateModel(ast.getComponentQName())) {
        // Add Builder attributes and getter for domain type parameters
        List<String> builders = new ArrayList<>();
        List<HookPoint> hps = new ArrayList<>();
        for (ASTGUIParam p : domainParams) {
          ASTCDAttribute cdAttr = addBuilderForDomainMembers(clazz, p);
          addAnnotation(cdAttr);
          domainAttributes.add(cdAttr);

          String builder = BuilderDecorator.getClassName(p.getName());
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

        // Add constructor
        addBuilderConstructor(clazz, domainAttributes, ast.getComponentQName());
      }
      else {
        // Add attributes
        List<HookPoint> hps = new ArrayList<>();
        for (ASTGUIParam p : domainParams) {
          ASTCDAttribute cdAttr = addAttributForGUIParameter(clazz, p, CDModifier.PUBLIC.build());
          addAnnotation(cdAttr);
          domainAttributes.add(cdAttr);

          if (util.getMainDomainModelSimpleName().isPresent()) {
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

        List<ASTGUIParam> otherParams = domainClass.getGUIParamList().stream()
            .filter(p -> !util.isDomainType(p)).collect(Collectors.toList());
        List<ASTCDAttribute> otherAttributes = new ArrayList<>();
        for (ASTGUIParam p : otherParams) {
          ASTCDAttribute cdAttr = addAttributForGUIParameter(clazz, p, CDModifier.PUBLIC.build());
          addAnnotation(cdAttr);
          otherAttributes.add(cdAttr);
        }
        List<String> params = new ArrayList<>();
        domainClass.forEachGUIParams(p -> params.add(
            getParam(p.getSymbol().getType().getTypeInfo().getName(), p.getName())
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

    return clazz;
  }

  protected void addDefaultConstructor(ASTCDClass clazz) {
    String className = clazz.getName();
    getCD4C().addConstructor(clazz, "umlp.domain.Constructor", className);
  }

  protected void addBuilderConstructor(
      ASTCDClass clazz, List<ASTCDAttribute> domainAttributes, String componentName
  ) {
    Optional<String> domainQName = util.getMainDomainModelQName();
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

  protected void addAnnotation(ASTCDAttribute attr) {
  }

  protected ASTCDMethod addInitMethod() {
    ASTCDMethod method = this.getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), "init");
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(getInitTemplate()));
    return method;
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

  protected String getParam(String type, String name) {
    return type + " " + name;
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam) {
    return addAttributForGUIParameter(clazz, astguiParam, CDModifier.PROTECTED.build());
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam, ASTModifier modifier) {
    // attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        modifier, astguiParam.getMCType(),
        util.gui2cdAttrName(astguiParam.getName())
    );
    addInitialValue(attr);
    clazz.addCDMember(attr);

    // getter
    String builderGetter = util.gui2cdAttrGetterName(astguiParam.getName());
    getCD4C().addMethod(clazz, getGetterParamTemplate(),
        builderGetter, astguiParam.getMCType().printType(), astguiParam.getName());
    return attr;
  }

  protected void addInitialValue(ASTCDAttribute attr) {
  }

  protected void addImportForGUIParameter(ASTCDClass clazz, String qName) {
    for (BuiltInTypes type : BuiltInTypes.values()) {
      if (type.getName().equals(qName)) {
        return;
      }
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

  protected boolean isModelImportInGUI(ASTMCImportStatement i) {
    return isCDModelImportInGUI(i) || isGUIModelImportInGUI(i);
  }

  protected boolean isGUIModelImportInGUI(ASTMCImportStatement i) { // TODO MB  guisym
    return GUIDSLMill.globalScope()
        .resolveGUIComponentDeclaration(i.getQName()).isPresent();
  }

  protected boolean isCDModelImportInGUI(ASTMCImportStatement i) { // TODO MB umlpsym UMLPDefSymbol
    return util.isDomainImport(i) || !isGUIModelImportInGUI(i);
  }

  public ASTCDClass getDecoratedClass() {
    return decoratedClass;
  }

}

/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.domain.decorator.commands.ManagerSubscriptionDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.util.PathUtil;
import umlp.generator.util.SingletonLookUp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.TopDecorator.TOP_SUFFIX;

public class GUI2TypeScriptAngularDecorator extends GUIAbstractDecorator {
  private final GUIComponentsVisitor viewComponents;

  private final boolean isInputGui;
  protected final MCPath hwPath;
  protected final Path outPath;

  public GUI2TypeScriptAngularDecorator(GlobalExtensionManagement glex, boolean isInputGui, GUIComponentsVisitor viewComponents, MCPath hwPath, Path outPath) {
    super(glex);
    this.isInputGui = isInputGui;
    this.viewComponents = viewComponents;
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    // package
    String packageName = getPackageName(guiAst, DEFAULT_PACKAGE);
    ASTCDPackage astcdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
    decoratedAST.getCDDefinition().addCDElement(astcdPackage);

    String originalName = guiAst.getComponentName();

    // class
    ASTCDClass decoratedClass = createClass(guiAst, packageName);
    astcdPackage.addCDElement(withOrigin(decoratedClass));

    // imports
    getCD4C().addImport(decoratedClass,packageName + "." + originalName);
    boolean addedManager = false;
    for (ASTMCImportStatement i : guiAst.getMCImportStatementList()) {
      if (!isGUIModelImportInGUI(i)) {
        // add manager import once
        if (!addedManager) {
          getCD4C().addImport(decoratedClass, util.gui2tsManagerImport(originalName));
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

    if (existsHandwrittenDomainClass(guiAst.getGUIComponentDeclaration(), guiAst)) {
      glex.replaceTemplate(
          GUI2ComponentTemplates.ANNOTATIONS,
          decoratedClass,
          new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_DIRECTIVE)
      );
    }
    else {
      String htmlFileName = originalName + ".html";
      String htmlPath = "./" + htmlFileName;
      String qName = decoratedAST.getMCPackageDeclaration().getMCQualifiedName().getQName()
          + "." + originalName;

      Optional<String> hwHtmlPath = util.getRelativeImportPath(
          hwPath, outPath, qName, "html");
      if (hwHtmlPath.isPresent()) {
        htmlPath = hwHtmlPath.get();
      }

      List<String> scssPaths = new ArrayList<>();

      Optional<String> hwScssPath = util.getRelativeImportPath(
          hwPath, outPath, qName, "scss");
      if (hwScssPath.isPresent()) {
        scssPaths.add(hwScssPath.get());
      }

      String selector = GUIGeneratorUtil.getInstance().getComponentNameFromCamelCaseName(originalName);

      glex.replaceTemplate(
          GUI2ComponentTemplates.ANNOTATIONS,
          decoratedClass,
          new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_COMPONENT, htmlPath, scssPaths, selector)
      );
    }
  }

  protected ASTCDClass createClass(ASTGUICompilationUnit ast, String packageName) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    // empty class
    String className = domainClass.getName() + GUIAbstractDecorator.COMPONENT_SUFFIX;
    String domainClassName = getDomainClassName(domainClass, ast);

    ASTCDClass clazz = UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage(domainClassName))
        .build();

    // imports
    getCD4C().addImport(clazz,packageName + "." + domainClassName);

    // constructor, attrs, getters
    if (!domainClass.getGUIParamList().isEmpty()) {
      addInitMethod(clazz);
      if(ast.getGUIComponentDeclaration().isPage()
          && domainClass.getGUIParamList().stream().anyMatch(util::isDomainListType)) {
        addDestroyMethod(clazz);
      }

      if(ast.getGUIComponentDeclaration().isPage()) {
        // Otherwise, the updates are provided by the parent component
        addUpdateObserverAndUpdateMethods(clazz, ast);
      }

      List<ASTGUIParam> domainParams = domainClass.getGUIParamList().stream()
          .filter(p -> util.isDomainType(p))
          .collect(Collectors.toList());
      List<ASTGUIParam> domainListParams = domainClass.getGUIParamList().stream()
          .filter(p -> util.isDomainListType(p))
          .collect(Collectors.toList());
      List<ASTCDAttribute> domainAttributes = new ArrayList<>();

      boolean updateObserverNeeded = false;
      for (ASTGUIParam p : domainListParams) {
        ASTCDAttribute cdAttr = addAttributForGUIParameter(clazz, p, CDModifier.PUBLIC.build());
        addAnnotation(cdAttr);
        domainAttributes.add(cdAttr);

        if (util.getMainDomainModelSimpleName().isPresent()) {
          // TODO AGe: body of init method currently uses different hooks, improve
          if(ast.getGUIComponentDeclaration().isPage()) {
            updateObserverNeeded = true;
            // Otherwise, parent page updates parameters
            String originalName = util.getMainDomainModelSimpleName().get();

            String domainType = util.getDomainListInnerType(p);
            glex.addAfterTemplate("umlp.gui2java.InitBody", new TemplateHookPoint(
                "umlp.gui2ts.TSInitDomainList",
                p.getName(),
                domainType,
                originalName + ManagerDecorator.SUFFIX));
            getCD4C().addImport(clazz, util.gui2tsGeneralImport(originalName, domainType));
            getCD4C().addImport(clazz, util.gui2tsGeneralImport(originalName, "commands." + domainType + ManagerSubscriptionDecorator.SUFFIX));
          }
        }
        else {
          Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
        }
      }

      if(updateObserverNeeded){
        glex.addAfterTemplate("umlp.gui2java.InitBody", new StringHookPoint(
            util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX + ".addObserver(this.updateObserver);"
            ));
        getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
      }

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
                "umlp.gui2ts.TSInitBuilder",
                builder,
                StringTransformations.uncapitalize(p.getMCType().printType()),
                util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX));
          }
          else {
            Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
          }

        }
        glex.setBeforeTemplate("umlp.gui2java.InitBody", hps);
//        getCD4C().addMethod(clazz, "umlp.gui2ts.TSBuild", builders); // TODO entfernen

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

          if (util.getMainDomainModelSimpleName().isPresent() && !util.isEnumType(p.getSymbol().getType())) {
            // TODO AGe: body of init method currently uses different hooks, improve
            if (SingletonLookUp.isSingleton(p.getSymbol().getType().getTypeInfo())) {
              hps.add(new TemplateHookPoint(
                      "umlp.gui2ts.TSInitDomainSingleton",
                      p.getName(),
                      p.getMCType().printType(),
                      util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX));
            } else {
              hps.add(new TemplateHookPoint(
                      "umlp.gui2ts.TSInitDomain",
                      p.getName(),
                      p.getMCType().printType(),
                      util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX));
            }
          }
          else {
            Log.warn("0x00007 Could not identify the manager name: domain classdiagram is missing");
          }
        }
        // fill init
        glex.setBeforeTemplate("umlp.gui2java.InitBody", hps);

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

        addEditConstructor(clazz, params, domainAttributes, otherAttributes, ast);
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
//      addDefaultConstructor(clazz);
    }

    return clazz;
  }

  protected void addUpdateObserverAndUpdateMethods(ASTCDClass clazz, ASTGUICompilationUnit ast) {
    List<ASTGUIParam> paramsTypedListOfDomain = ast.getGUIComponentDeclaration().getGUIParamList().stream()
        .filter(util::isDomainListType)
        .collect(Collectors.toList());

    List<String> types = paramsTypedListOfDomain.stream()
        .map(util::getDomainListInnerType)
        .collect(Collectors.toList());

    String originalName = util.getMainDomainModelSimpleName().get();
    getCD4C().addAttributeFromTemplate(clazz, "umlp.gui2ts.UpdateObserverAttr", originalName,types);
    getCD4C().addImport(clazz, util.gui2tsGeneralImport(originalName, originalName + "Observer"));
    getCD4C().addImport(clazz, util.gui2tsGeneralImport(originalName, "Empty" + originalName + "Observer"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    if (UMLPGeneratorConfig.getInstance().isUseJsweet()) {
      getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("UMLPArrayList"));
    }

    // Update methods
    for (var p : paramsTypedListOfDomain) {
      getCD4C().addMethod(clazz, "umlp.gui2ts.UpdateListOfDomain",
          p.getName(),
          util.getDomainListInnerType(p),
          util.getMainDomainModelSimpleName().get() + ManagerDecorator.SUFFIX);
      getCD4C().addMethod(clazz, "umlp.gui2ts.TSAddToListOfDomain",
          p.getName(),
          util.getDomainListInnerType(p));
      getCD4C().addMethod(clazz, "umlp.gui2ts.TSRemoveFromListOfDomain",
          p.getName(),
          util.getDomainListInnerType(p));
    }
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam, ASTModifier modifier) {
    // attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        modifier, transformTypeForAttr(clazz, astguiParam),
        util.gui2cdAttrName(astguiParam.getName())
    );
    glex.replaceTemplate(GUI2ComponentTemplates.ATTRIBUTE, attr,
        new TemplateHookPoint(GUI2ComponentTemplates.OVERRIDEATTRIBUTE));
    addInitialValue(attr);
    clazz.addCDMember(attr);
    return attr;
  }

  protected void addInitMethod(ASTCDClass clazz) {
    ASTCDMethod method = this.getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), "ngOnInit");
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2ts.TSInit"));
    clazz.addCDMember(method);
  }

  protected void addDestroyMethod(ASTCDClass clazz) {
    getCD4C().addMethod(clazz, "umlp.gui2ts.TSDestroy", util.getMainDomainModelSimpleName().get() + "Manager");
  }

  // TODO AGe: own event decorator
  protected void addInitialValue(ASTCDAttribute attr) {
    if (attr.getMCType().printType().equals(BuiltInTypes.EVENT.getQName())
        || attr.getMCType().printType().equals(BuiltInTypes.EVENT.getName())
    ) {
      glex.replaceTemplate("cd2java.Value", attr,
          new StringHookPoint(" = new EventEmitter<any>()"));
    }
  }

  protected void addAnnotation(ASTCDAttribute attr) {
    glex.replaceTemplate(
        GUI2ComponentTemplates.ANNOTATIONS,
        attr,
        new TemplateHookPoint(GUI2ComponentTemplates.ANNOTATION_INPUT)
    );
  }

  @Override
  protected ASTMCType transformTypeForAttr(ASTCDClass clazz, ASTGUIParam param) {
    if (util.isDomainListType(param)) {
      String inner = util.getDomainListInnerType(param);
      getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
      return MCTypeFacade.getInstance().createBasicGenericTypeOf("GemList", inner);
    }
    return param.getMCType();
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
    getCD4C().addMethod(clazz, "umlp.gui2ts.TSGetParam", builderGetter, attributeType, attributeName);

    return domainAttribute;
  }

  protected void addBuilderConstructor(
      ASTCDClass clazz, List<ASTCDAttribute> domainAttributes, String componentName
  ) {
    Optional<String> domainQName = util.getMainDomainModelQName();
    if (domainQName.isPresent()) {
      String className = clazz.getName();
      glex.replaceTemplate("umlp.gui2ts.TSConstructor", clazz,
          new TemplateHookPoint("umlp.gui2ts.TSConstructorWithBuilder", domainQName.get(),
              className, domainAttributes, guiDSLPrinter)
      );
      /*
      getCD4C().addConstructor(clazz, getConstructorWithBuilderTemplate(),
          domainQName.get(), className, // TODO MB: Diagramm name UMLPDefSymbol
          domainAttributes, guiDSLPrinter);
          /**/
    }
    else {
      Log.error("0x00000 GUI Model " + componentName
          + " is missing a connection to the domain model."
          + " Please specify the domain model using '-dm' flag.");
    }
  }

  protected void addEditConstructor(ASTCDClass clazz, List<String> params,
      List<ASTCDAttribute> domainAttributes, List<ASTCDAttribute> otherAttributes,
      ASTGUICompilationUnit guiAst) {
    List<String> ids = util.getRoutingIds(guiAst);
    glex.replaceTemplate("umlp.gui2ts.TSConstructor", clazz,
        new TemplateHookPoint("umlp.gui2ts.TSEditConstructor", ids));
    for (String id : ids) {
      getCD4C().addAttribute(clazz, "public int " + id);
    }
  }

  public String getDomainClassName(ASTGUIComponentDeclaration domainClass, ASTGUICompilationUnit ast) {
    String domainClassName = Names.getQualifiedName(ast.getPackage(), domainClass.getName());
    return domainClass.getName() + (GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, domainClassName)? TOP_SUFFIX :"");
  }

  public boolean existsHandwrittenDomainClass(ASTGUIComponentDeclaration domainClass, ASTGUICompilationUnit ast) {
    String domainClassName = Names.getQualifiedName(ast.getPackage(), domainClass.getName());
    return GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, domainClassName);
  }
}

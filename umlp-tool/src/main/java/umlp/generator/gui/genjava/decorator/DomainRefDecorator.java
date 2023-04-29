/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.ErrorCollectorDecorator;
import umlp.generator.domain.decorator.ErrorObserverDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.gui.GUIGeneratorUtil.getGetterName;
import static umlp.generator.gui.GUIGeneratorUtil.isDomainType;

public class DomainRefDecorator extends GUIAbstractDecorator {

  private final boolean isInputGui;

  private Optional<ASTCDClass> createInputValueClass = Optional.empty();

  private ASTCDClass decoratedClass;

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
    guiAst.getMCImportStatementList().stream()
        .filter(i -> !isModelImportInGUI(i))
        .forEach(i -> getCD4C().addImport(decoratedClass, i.getQName()));
  }

  protected void createBuildMethod(ASTCDClass clazz, List<ASTGUIParam> collect) {
    getCD4C().addMethod(clazz, "umlp.gui2java.Build", collect.stream()
        .map(p -> BuilderDecorator.getClassName(p.getName()))
        .collect(Collectors.toList()));
  }

  protected ASTCDAttribute addBuilderForDomainMembers(ASTCDClass clazz, ASTGUIParam astguiParam) {
    // attr
    // --- replace this when TypeCheck can be used
    TypeSymbol typeInfo = astguiParam.getSymbol().getType().getTypeInfo();

    String qualifiedType = BuilderDecorator.getClassName(typeInfo.getFullName()); // domainTypeSymbol.getQualifiedName(); //TODO MB
    String attributeType = BuilderDecorator.getClassName(typeInfo.getName());
    String attributeName = astguiParam.getName() + "Builder";
    // --- end

    getCD4C().addImport(clazz, qualifiedType);

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), attributeType, attributeName);

    clazz.addCDMember(domainAttribute);

    // gettter
    String builderGetter = GUIGeneratorUtil.getGetterName(astguiParam);
    getCD4C().addMethod(clazz, "umlp.gui2java.GetParam", builderGetter, attributeType, attributeName);

    return domainAttribute;
  }

  protected ASTCDClass createClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    // empty class
    String className = domainClass.getName();
    domainClass.getName();
    ASTCDClass clazz = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .build();

    // constructor, attrs, getters
    if (!domainClass.getGUIParamList().isEmpty()) {
      List<ASTGUIParam> domainParams = domainClass.getGUIParamList().stream()
          .filter(p -> isDomainType(p))
          .collect(Collectors.toList());
      List<ASTCDAttribute> domainAttributes;
      // attrs, getters
      if (GUIGeneratorUtil.isCreateModel(ast.getComponentQName())) {
        // Add Builder attributes and getter for domain type parameters
        domainAttributes = domainParams.stream()
            .map(p -> addBuilderForDomainMembers(clazz, p))
            .collect(Collectors.toList());

        // Add build method
        createBuildMethod(clazz, domainParams);

        // Add constructor
        Optional<String> domainQName = GUIGeneratorUtil.getMainDomainModelQName();
        if (domainQName.isPresent()) {
          getCD4C().addConstructor(clazz, "umlp.gui2java.ConstructorWithBuilder",
              domainQName.get(), className, // TODO MB: Diagramm name UMLPDefSymbol
              domainAttributes, guiDSLPrinter);
        }
        else {
          Log.error("0x00000 GUI Model " + ast.getComponentQName()
              + " is missing a connection to the domain model."
              + " Please specify the domain model using '-dm' flag.");
        }
      }
      else {
        // Add attributes
        domainAttributes = domainParams.stream()
            .map(p -> addAttributForGUIParameter(clazz, p))
            .collect(Collectors.toList());

        List<ASTGUIParam> otherParams = domainClass.getGUIParamList().stream().filter(p -> !isDomainType(p)).collect(Collectors.toList());
        List<ASTCDAttribute> otherAttributes = otherParams.stream().map(p -> addAttributForGUIParameter(clazz, p)).collect(Collectors.toList());
        List<String> params = new ArrayList<>();
        domainClass.forEachGUIParams(p -> params.add(p.getSymbol().getType().getTypeInfo().getName() + " " + p.getName()));

        getCD4C().addConstructor(clazz, "umlp.gui2java.Constructor", className,
            Joiner.on(",").join(params), domainAttributes, otherAttributes,
            guiDSLPrinter, isInputGui);
      }

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

      ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), "init");
      glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2java.Init"));
      clazz.addCDMember(method);
    }
    else {
      getCD4C().addConstructor(clazz, "umlp.domain.Constructor", className);
    }

    return clazz;
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam) {
    SymTypeExpression type = astguiParam.getSymbol().getType();
    for (String qName : getImport(type)) {
      getCD4C().addImport(clazz, qName);
    }

    // attr
    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), astguiParam.getMCType(), astguiParam.getName());
    clazz.addCDMember(domainAttribute);

    // getter
    String builderGetter = GUIGeneratorUtil.getGetterName(astguiParam);
    getCD4C().addMethod(clazz, "umlp.gui2java.GetParam",
        builderGetter, astguiParam.getMCType().printType(), astguiParam.getName());
    return domainAttribute;
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
    // TODO AGe: not only domain imports are cd imports
    return GUIGeneratorUtil.isDomainImport(i);
  }

  public Optional<ASTCDClass> getCreateInputValueClass() {
    return createInputValueClass;
  }

  public ASTCDClass getDecoratedClass() {
    return decoratedClass;
  }

}

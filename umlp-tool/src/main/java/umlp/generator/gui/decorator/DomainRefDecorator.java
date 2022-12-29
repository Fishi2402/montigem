/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.decorator;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import umlp.UMLPMill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.gui.GUIGeneratorUtil.isDomainType;

public class DomainRefDecorator extends GUIAbstractDecorator {

  public static final String ERROR_OBSERVER = "ErrorObserver";
  public static final String ERROR_COLLECTOR = "ErrorCollector";
  public static final String BUILDER = "Builder";

  public static final String DOMAIN_PACKAGE = "hardy."; //TODO: remove it when packaging in umlp-symtab does properly

  private boolean isInputGui;

  private Optional<ASTCDClass> createInputValueClass = Optional.empty();

  private ASTCDClass decoratedClass;

  public DomainRefDecorator(final GlobalExtensionManagement glex, boolean isInputGui) {
    super(glex);
    this.isInputGui = isInputGui;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    String packageName = getPackageName(guiAst, DEFAULT_PACKAGE);
    ASTCDPackage astcdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
    decoratedAST.getCDDefinition().addCDElement(astcdPackage);

    String decoratedClassName = getComponentClassName(guiAst.getGUIComponentDeclaration(), isInputGui);

    decoratedClass = createClass(guiAst.getGUIComponentDeclaration(), decoratedClassName);
    astcdPackage.addCDElement(decoratedClass);

    guiAst.getMCImportStatementList().stream().filter(i -> !isModelImport(i)).forEach(i -> getCD4C().addImport(decoratedClass, i.getQName()));

    if (isInputGui) {
      createInputValueClass = createNewInputClass(guiAst.getGUIComponentDeclaration());
      astcdPackage.addCDElement(createInputValueClass.get());

      guiAst.getMCImportStatementList().stream().filter(i -> !isModelImport(i)).forEach(i -> getCD4C()
          .addImport(createInputValueClass.get(), i.getQName()));
    }
  }

  protected Optional<ASTCDClass> createNewInputClass(ASTGUIComponentDeclaration domainClass) {
    String className = domainClass.getName() + CREATE_INPUT_VALUE_SUFFIX;

    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className);
    ASTCDClass clazz = builder.build();

    if (!domainClass.getGUIParamList().isEmpty()) {
      List<ASTGUIParam> domainParams = domainClass.getGUIParamList().stream().filter(p -> isDomainType(p)).collect(Collectors.toList());

      // Add Builder attributes for domain type parameters
      List<ASTCDAttribute> domainAttributes = new ArrayList<>();
      domainParams.forEach(p -> domainAttributes.add(addBuilderForDomainMembers(clazz, p, className)));

      // Add build method
      createBuildMethod(clazz, domainParams);

      // Add constructor
      getCD4C().addConstructor(clazz, "umlp.gui2java.ConstructorWithBuilder", "hardy.Flensburg", className, // TODO MB: Diagramm name UMLPDefSymbol
          domainAttributes, guiDSLPrinter);
    }
    else {
      getCD4C().addConstructor(clazz, "umlp.domain.Constructor", className);
    }

    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(),  "init");
    glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2java.Init"));
    clazz.addCDMember(method);

    return Optional.of(clazz);
  }

  protected void createBuildMethod(ASTCDClass clazz, List<ASTGUIParam> collect) {
    getCD4C().addMethod(clazz, "umlp.gui2java.Build", collect.stream().map(p -> p.getName() + BUILDER).collect(Collectors.toList()));
  }

  protected ASTCDAttribute addBuilderForDomainMembers(ASTCDClass clazz, ASTGUIParam astguiParam, String className) {
    // --- replace this when TypeCheck can be used

    //  String domainTypeName = astguiParam.getMCType().printType(basicTypesPrinter);  // TODO GV
    String attributeType = new GUIDSLFullPrettyPrinter().prettyprint(astguiParam.getMCType());

    TypeSymbol typeInfo = astguiParam.getSymbol().getType().getTypeInfo();

    // TODO when ST ist fertig: qualified domainTypeName und add import declaration
    String qualifiedType = DOMAIN_PACKAGE + typeInfo.getName() + BUILDER; // domainTypeSymbol.getQualifiedName(); //TODO MB
    String qualifiedTypeP = typeInfo.getFullName();
    // --- end

    getCD4C().addImport(clazz, qualifiedType);

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
        attributeType + BUILDER, astguiParam.getName() + BUILDER);

    clazz.addCDMember(domainAttribute);
    return domainAttribute;
  }

  protected ASTCDClass createClass(ASTGUIComponentDeclaration domainClass, String decoratedClassName) {
    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(decoratedClassName);
    ASTCDClass clazz = builder.build();

    if (!domainClass.getGUIParamList().isEmpty()) {

      List<ASTGUIParam> domainParams = domainClass.getGUIParamList().stream().filter(p -> isDomainType(p)).collect(Collectors.toList());
      List<ASTGUIParam> otherParams = domainClass.getGUIParamList().stream().filter(p -> !isDomainType(p)).collect(Collectors.toList());

      // Add attributes
      List<ASTCDAttribute> domainAttributes = domainParams.stream().map(p -> addAttributForGUIParameter(clazz, p)).collect(Collectors.toList());
      List<ASTCDAttribute> otherAttributes = otherParams.stream().map(p -> addAttributForGUIParameter(clazz, p)).collect(Collectors.toList());
      List<String> params = new ArrayList<>();
      domainClass.forEachGUIParams(p -> params.add(p.getSymbol().getType().getTypeInfo().getName() + " " + p.getName()));

      Optional<ASTCDMethodSignature> constr = getCD4C().createConstructor(clazz, "umlp.gui2java.Constructor", decoratedClassName,
          Joiner.on(",").join(params), domainAttributes, otherAttributes, guiDSLPrinter);
      clazz.addCDMember(constr.get());

      List<ASTMCQualifiedType> observers = domainAttributes.stream().map(a -> getMCTypeFacade().createQualifiedType(guiDSLPrinter.prettyprint(a.getMCType()) + ERROR_OBSERVER)).collect(Collectors.toList());
      if (!observers.isEmpty()) {
        clazz.setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(Lists.newArrayList()));
        clazz.getCDInterfaceUsage().addAllInterface(observers);
      }

      domainAttributes.forEach(a -> getCD4C().addImport(clazz, DOMAIN_PACKAGE + guiDSLPrinter.prettyprint(a.getMCType()) + ERROR_OBSERVER));
      domainAttributes.forEach(a -> getCD4C().addImport(clazz, DOMAIN_PACKAGE + guiDSLPrinter.prettyprint(a.getMCType()) + ERROR_COLLECTOR));

      ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), "init");
      glex.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("umlp.gui2java.Init"));
      clazz.addCDMember(method);

   }
    else {
      getCD4C().addConstructor(clazz, "umlp.domain.Constructor", decoratedClassName);
    }


    return clazz;
  }

  protected ASTCDAttribute addAttributForGUIParameter(ASTCDClass clazz, ASTGUIParam astguiParam) {
    // --- replace this when TypeCheck can be used

    //  String domainTypeName = astguiParam.getMCType().printType(basicTypesPrinter);  // TODO GV
    String attributeType = new GUIDSLFullPrettyPrinter().prettyprint(astguiParam.getMCType());
    SymTypeExpression type = astguiParam.getSymbol().getType();

    // --- end

    getCD4C().addImport(clazz, getImport(type));

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
        attributeType, astguiParam.getName());
    clazz.addCDMember(domainAttribute);

    return domainAttribute;
  }

  public String getImport(SymTypeExpression type) { //TODO GV: return List<String> for HashMap usw.
    if (!type.isGenericType()) {
      return DOMAIN_PACKAGE + type.getTypeInfo().getName();  //TODO NJ: muss funktionieren  "return typeInfo.getFullName();"
    }
    List<SymTypeExpression> arguments = ((SymTypeOfGenerics) type).getArgumentList();
    if (arguments.isEmpty()) {
      return "";
    }
    return DOMAIN_PACKAGE + arguments.get(0).getTypeInfo().getName();   //TODO NJ: muss funktionieren  "return typeInfo.getFullName();"
  }

  protected boolean isModelImport(ASTMCImportStatement i) {
    return isCD(i) || isStandardGUIComponent(i);
  }

  protected boolean isStandardGUIComponent(ASTMCImportStatement i) { // TODO MB  guisym
    return i.getQName().startsWith("umlp.gui");
  }

  protected boolean isCD(ASTMCImportStatement i) { // TODO MB umlpsym UMLPDefSymbol
    return i.getQName().startsWith("hardy.Flensburg");
  }

  public Optional<ASTCDClass> getCreateInputValueClass() {
    return createInputValueClass;
  }

  public ASTCDClass getDecoratedClass() {
    return decoratedClass;
  }

}

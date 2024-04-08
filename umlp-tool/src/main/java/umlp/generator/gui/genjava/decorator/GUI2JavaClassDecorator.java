/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIList;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUI2JavaClassDecorator extends GUIAbstractDecorator implements GUIDSLVisitor2, CommonExpressionsVisitor2 {

  public static final String GUARD_ARG = "condition";
  public static final String ITERATE_ARG = "iterable";

  protected ASTCDClass clazz;
  protected GUIDSLTraverser traverser;
  protected ASTGUICompilationUnit gui;

  public GUI2JavaClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
  }

  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    this.gui = gui;

    // package
    ASTCDPackage cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(gui.getMCPackageDeclaration().getMCQualifiedName())
        .build();
    cd.getCDDefinition().addCDElement(cdPackage);

    // class
    clazz = emptyClass(gui);
    cdPackage.addCDElement(withOrigin(clazz));

    addConstructor();
    addInit();

    // TODO add router
    // addAdditionalAttributes(clazz);

    StaticObjectsCollector.getInstance().init(gui.getGUIComponentDeclaration());
    gui.accept(traverser);
  }

  @Override
  public void endVisit(ASTGUIParam node) {
    // calc type
    if (!node.isPresentSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + node.getName()
          + "' at " + node.get_SourcePositionStart().toString()
          + " - " + node.get_SourcePositionEnd().toString()
      );
    }
    SymTypeExpression typeSTE = node.getSymbol().getType();
    String type = importAndPrintType(typeSTE);

    // calc name
    String name = node.getName();

    // add attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), type, "_" + name);
    clazz.addCDMember(attr);

    // add getter
    getCD4C().addMethod(clazz, getParamGetterTemplate(), type, name);
    // add setter
    getCD4C().addMethod(clazz, getParamSetterTemplate(), type, name);
    // add attr init
    getCD4C().addMethod(clazz, getInitParamTemplate(), type, name);

    if (util.isDomainType(typeSTE)) {
      if (util.getMainDomainModelSimpleName().isEmpty()) {
        Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
        return;
      }
      String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());
      // add builder init
      ASTCDMethodSignature builderInit = getCD4C().addMethod(
          clazz, getInitParamBuilderTemplate(),
          type, name, manager, Collections.emptyList()
      );

      // add builder init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), builderInit)
      );
    }
    else if (util.isDomainListType(typeSTE)) {
      if (util.getMainDomainModelSimpleName().isEmpty()) {
        Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
        return;
      }
      String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());
      // add attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDomainListTemplate(), type, name, manager
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), attrInit)
      );
    }
    else {
      Optional<String> value = Optional.empty();
      if (node.isPresentDefault()) {
        value = Optional.of(util.print(node.getDefault()));
      }
      // add attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDefaultParamTemplate(), name, value
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), attrInit)
      );
    }
  }

  @Override
  public void endVisit(ASTGUIComponent node) {
    for (ASTGUINamedArg arg : node.getGUINamedArgs().getGUINamedArgList()) {
      // calc type
      if (!arg.isPresentNameSymbol()) {
        Log.error("0x0000E Unable to find symbol for '" + arg.getName()
            + "' at " + arg.get_SourcePositionStart().toString()
            + " - " + arg.get_SourcePositionEnd().toString()
        );
      }
      SymTypeExpression typeSTE = arg.getNameSymbol().getType();
      String type = importAndPrintType(typeSTE);

      // calc name
      String name = node.getId().getName() + StringTransformations.capitalize(arg.getName());

      // add getter
      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));

      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, iterateItems
      );
      Expression2SetterPrinter setterPrinter = new Expression2MutatorPrinter(
          new IndentPrinter(), false, iterateItems, iterateItems
      );
      Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

      String nullCheck = nullCheckPrinter.prettyprint(arg.getGUIArgValue());
      String getterValue = getterPrinter.prettyprint(arg.getGUIArgValue());

      // calc iterate items as additional args
      List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(arg.getEnclosingScope());
      for (VariableSymbol vs : iterateArgs) {
        importAndPrintType(vs.getType());
      }

      Optional<TypeSymbol> eventType = node.getEnclosingScope()
          .resolveType(BuiltInTypes.EVENT.getQName());
      if (eventType.isPresent() && typeSTE.getTypeInfo().equals(eventType.get())) {
        // add event caller
        getCD4C().addMethod(
            clazz, getEventCallerTemplate(),
            name, getterValue, iterateArgs
        );
      }
      else {
        // add getter
        getCD4C().addMethod(
            clazz, getArgGetterTemplate(),
            type, name, nullCheck, getterValue, iterateArgs
        );
      }

      if (arg.getNameSymbol().isIsReference()) {
        // add setter
        String setterValue = setterPrinter.prettyprint(arg.getGUIArgValue());
        getCD4C().addMethod(
            clazz, getArgSetterTemplate(),
            type, name, nullCheck, setterValue, iterateArgs
        );
      }
    }
  }

  @Override
  public void endVisit(ASTGUIGuard node) {
    // calc type
    SymTypeExpression typeSTE = util.getType(node.getCondition());
    String type = importAndPrintType(typeSTE);

    // calc name
    String name = node.getId().getName() + StringTransformations.capitalize(GUARD_ARG);

    // add getter
    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));

    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, iterateItems
    );
    Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

    String nullCheck = nullCheckPrinter.prettyprint(node.getCondition());
    String getterValue = getterPrinter.prettyprint(node.getCondition());

    // calc iterate items as additional args
    List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(node.getEnclosingScope());
    for (VariableSymbol vs : iterateArgs) {
      importAndPrintType(vs.getType());
    }

    // add getter
    getCD4C().addMethod(
        clazz, getArgGetterTemplate(),
        type, name, nullCheck, getterValue, iterateArgs
    );
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (!(node.getIterateControl() instanceof ASTGUIShortIterateControl)) {
      Log.error("0x00004 iterate construct " + node.getId().getName()
          + " was not processed: range iterate control not supported yet");
    }
    ASTGUIShortIterateControl control = (ASTGUIShortIterateControl) node.getIterateControl();
    // calc type
    SymTypeExpression typeSTE = util.getType(control.getIterable());
    String type = importAndPrintType(typeSTE);

    // calc name
    String name = node.getId().getName() + StringTransformations.capitalize(ITERATE_ARG);

    // add getter
    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));

    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, iterateItems
    );
    Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

    String nullCheck = nullCheckPrinter.prettyprint(control.getIterable());
    String getterValue = getterPrinter.prettyprint(control.getIterable());

    // calc iterate items as additional args
    List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(node.getEnclosingScope());
    for (VariableSymbol vs : iterateArgs) {
      importAndPrintType(vs.getType());
    }

    // add getter
    getCD4C().addMethod(
        clazz, getArgGetterTemplate(),
        type, name, nullCheck, getterValue, iterateArgs
    );
  }

  @Override
  public void endVisit(ASTGUIList node) {
    Map<ASTNode, ASTCDAttribute> node2Attr = StaticObjectsCollector
        .getInstance().getNode2Attr();
    if (node2Attr.containsKey(node)) {
      // add attr
      ASTCDAttribute attr = node2Attr.get(node);
      clazz.addCDMember(attr);

      // add getter
      getCD4C().addMethod(clazz, getGetterTemplate(), attr.getMCType().printType(), attr.getName());

      // add attr init
      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, Collections.emptyList(), Collections.emptyList()
      );
      List<String> listItems = new ArrayList<>();
      for (ASTGUIArgValue val : node.getGUIArgValueList()) {
        listItems.add(getterPrinter.prettyprint(val));
      }

      // calc iterate items as additional args
      List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(node.getEnclosingScope());
      for (VariableSymbol vs : iterateArgs) {
        importAndPrintType(vs.getType());
      }

      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitListTemplate(), attr, listItems, iterateArgs
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), attrInit)
      );
    }
  }

  @Override
  public void endVisit(ASTCallExpression node) {
    Map<ASTNode, ASTCDAttribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();
    if (node2Attr.containsKey(node)) {
      // add attr
      ASTCDAttribute attr = node2Attr.get(node);
      String type = attr.getMCType().printType();
      String name = attr.getName();
      clazz.addCDMember(attr);

      // add getter
      getCD4C().addMethod(clazz, getGetterTemplate(), attr.getMCType().printType(), attr.getName());

      if (util.getMainDomainModelSimpleName().isPresent()) {
        String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());

        // calc iterate items as additional args
        List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(node.getEnclosingScope());
        for (VariableSymbol vs : iterateArgs) {
          importAndPrintType(vs.getType());
        }

        // add builder init
        ASTCDMethodSignature builderInit = getCD4C().addMethod(
            clazz, getInitBuilderTemplate(),
            type, name, manager, iterateArgs
        );

        // add builder init to class init
        glex.addAfterTemplate(
            getInitBodyHookpoint(),
            new TemplateHookPoint(getInitCallTemplate(), builderInit)
        );
      }
    }
    // a custom function or a predefined one
    else if (node.getExpression() instanceof ASTNameExpression) {
      CallTypeDeriver deriver = CallTypeDeriver.getInstance();
      deriver.init(gui.getGUIComponentDeclaration(), node);
      if (deriver.isPresentResult()) {
        String name = ((ASTNameExpression) node.getExpression()).getName();
        String type = deriver.getResult().print();

        List<Argument> arguments = new ArrayList<>();
        for (int i = 0; i < node.getArguments().getExpressionList().size(); ++i) {
          ASTExpression exp = node.getArguments().getExpressionList().get(i);
          SymTypeExpression argSTE = util.getType(exp);
          if (argSTE.isObscureType()) {
            Log.error("0x00015 Unable to derive the type of expression "
                + util.print(node)
                + "' at " + node.get_SourcePositionStart().toString()
                + " - " + node.get_SourcePositionEnd().toString()
            );
          }
          importAndPrintType(argSTE);
          arguments.add(new Argument(type, name));
        }
        importAndPrintType(deriver.getResult());
        getCD4C().addMethod(clazz, getEmptyCallTemplate(), type, name, arguments);
      }
      else {
        Log.error("0x00014 Unable to derive the type of call "
            + util.print(node)
            + "' at " + node.get_SourcePositionStart().toString()
            + " - " + node.get_SourcePositionEnd().toString()
            + ". Please make sure that the call is a direct value of"
            + " a GUI component parameter, "
            + " a GUI component argument, "
            + " a guard, "
            + " an iterable item, "
            + " or is inside a block."
        );
      }
    }
  }

  protected ASTCDClass emptyClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    String className = domainClass.getName();
    return UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .build();
  }

  protected ASTCDMethodSignature addConstructor() {
    return getCD4C().addConstructor(clazz, "umlp.gui2java.GUI2JavaConstructor");
  }

  protected ASTCDMethodSignature addInit() {
    return getCD4C().addMethod(clazz, "umlp.gui2java.GUI2JavaInit");
  }

  protected String importAndPrintType(SymTypeExpression ste) {
    if (ste.isObjectType()) {
      // given in GUI:
      // import hardy.Domain.Person;
      // the resolved type is `hardy.Person`
      // but the CD Generator produces `hardy.domain.Person`, so calculate that instead
      IBasicSymbolsScope scope = ste.getTypeInfo().getEnclosingScope();
      if (scope.isPresentName()
          && util.getMainDomainModelSimpleName().isPresent()
          && scope.getName().equals(util.getMainDomainModelSimpleName().get())) {

        String insert = StringTransformations.uncapitalize(scope.getName()) + ".";
        String qTypeName = ste.asObjectType().printFullName();

        int lastIndex = qTypeName.lastIndexOf('.');

        if (lastIndex != -1) {
          StringBuilder sb = new StringBuilder(qTypeName);
          sb.insert(lastIndex + 1, insert);
          qTypeName = sb.toString();
        }
        else {
          qTypeName = insert + qTypeName;
        }
        getCD4C().addImport(clazz, qTypeName);
      }
      else {
        getCD4C().addImport(clazz, ste.printFullName());
      }
    }
    else if (ste.isGenericType()) {
      SymTypeOfGenerics stog = ste.asGenericType();

      getCD4C().addImport(clazz, stog.getTypeConstructorFullName());

      for (SymTypeExpression steArg : stog.getArgumentList()) {
        importAndPrintType(steArg);
      }
    }
    else if (ste.isPrimitive()) {
      return ste.asPrimitive().getBoxedPrimitiveName();
    }
    else if (!(ste.isNullType() || ste.isVoidType() || ste.isWildcard())) {
      Log.error("0x00010 Encountered unsupported type in " + gui.getComponentQName()
          + ": parameter type '" + ste.printFullName() + "'");
    }
    return ste.print();
  }

  protected String getInitParamTemplate() {
    return "umlp.gui2java.GUI2JavaInitParam";
  }

  protected String getInitDefaultParamTemplate() {
    return "umlp.gui2java.GUI2JavaInitDefaultParam";
  }

  protected String getInitDomainListTemplate() {
    return "umlp.gui2java.GUI2JavaInitDomainList";
  }

  protected String getInitParamBuilderTemplate() {
    return "umlp.gui2java.GUI2JavaInitParamBuilder";
  }

  protected String getInitBuilderTemplate() {
    return "umlp.gui2java.GUI2JavaInitBuilder";
  }

  protected String getInitListTemplate() {
    return "umlp.gui2java.GUI2JavaInitList";
  }

  protected String getInitCallTemplate() {
    return "umlp.gui2java.GUI2JavaInitCall";
  }

  protected String getInitBodyHookpoint() {
    return "umlp.gui2java.GUI2JavaInitBody";
  }

  protected String getParamGetterTemplate() {
    return "umlp.gui2java.GUI2JavaParamGetter";
  }

  protected String getParamSetterTemplate() {
    return "umlp.gui2java.GUI2JavaParamSetter";
  }

  protected String getArgGetterTemplate() {
    return "umlp.gui2java.GUI2JavaArgGetter";
  }

  protected String getArgSetterTemplate() {
    return "umlp.gui2java.GUI2JavaArgSetter";
  }

  protected String getGetterTemplate() {
    return "umlp.gui2java.GUI2JavaGetter";
  }

  protected String getEmptyCallTemplate() {
    return "umlp.gui2java.GUI2JavaCall";
  }

  protected String getEventCallerTemplate() {
    return "umlp.gui2java.GUI2JavaEventCaller";
  }

}

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
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.ManagerDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIGeneratorUtil;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Turn a GUI AST into a CD AST containing one class. The class manages component state and
 * implements some logic, such as navigation to pages, creation of objects, etc.
 */
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

  /**
   * Main function that transforms GUI AST into CD AST. Transformation stages:
   * 1. create empty class
   * 2. add directive annotation
   * 3. traverse GUI AST and add class members in a reverse order
   * 4. add constructor
   * 5. add initialization method
   * Adding members includes adding initialization functions for the attributes. These functions
   * must be called in a specific order in the class initialization method, namely in reverse.
   *
   * @param gui input GUI AST
   * @param cd output CD AST
   */
  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    this.gui = gui;
    glex.replaceTemplate("cd2java.Imports", new TemplateHookPoint("umlp.Imports"));

    // package
    ASTCDPackage cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(gui.getMCPackageDeclaration().getMCQualifiedName())
        .build();
    cd.getCDDefinition().addCDElement(cdPackage);

    // class
    clazz = emptyClass(gui);
    cdPackage.addCDElement(withOrigin(clazz));
    // directive
    addDirectiveAnnotation();

    // TODO add router
    // addAdditionalAttributes(clazz);

    StaticObjectsCollector.getInstance().init(gui.getGUIComponentDeclaration());
    gui.accept(traverser);

    // constructor
    addConstructor();
    // init
    addInit();
  }

  /**
   * Each input parameter (with one exception) is transformed into
   * - attribute
   * - getter
   * - setter
   * - 2 init functions
   * One init function does not require an input parameter and sets a default value of a parameter.
   * Input parameters representing domain objects receive a value loaded from a server.
   * Another init function takes an argument and delegates to the setter.
   * `GUIViewElement` and its collections are not transformed, since they do not represent the state
   * of a component, but rather its appearance (useless in the context of HWC/testing)
   *
   * @param node input parameter of a component
   */
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
    // exclude GUIViewElement
    if (GUIGeneratorUtil.getInstance().isViewElement(typeSTE)
        || GUIGeneratorUtil.getInstance().isViewElementsCollection(typeSTE)) {
      return;
    }
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

    // case 1: component Foo(Person p)
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
    // case 2: component Foo(List<Person> p)
    else if (util.getDomainListInnerType(typeSTE).isPresent()
        && !util.isEnumType(util.getDomainListInnerType(typeSTE).get())) {
      if (util.getMainDomainModelSimpleName().isEmpty()) {
        Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
        return;
      }
      String innerType = util.getDomainListInnerType(typeSTE).get().print();
      String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());
      // add attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDomainListTemplate(), innerType, name, manager
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), attrInit)
      );
    }
    // case 3: component Foo(String s)
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

  /**
   * An argument is transformed into a getter.
   * If the argument holds a reference, i.e., argument corresponds to a parameter like `?int value`,
   * it is also transformed into a setter (used for input fields or forms).
   * If the argument is an `Event` it is transformed into a call instead of a getter, e.g.,
   * `foo@GemButton(leftClick = action())` is transformed into `fooLeftClick` method that calls
   * whatever is on the right side of the assignment.
   * `GUIViewElement` and its collections are not transformed, since they do not represent the state
   * of a component, but rather its appearance (useless in the context of HWC/testing)
   *
   * @param node argument passed to a component
   */
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
      if (GUIGeneratorUtil.getInstance().isViewElement(typeSTE)
          || GUIGeneratorUtil.getInstance().isViewElementsCollection(typeSTE)) {
        return;
      }
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

      if (typeSTE.printFullName().startsWith(BuiltInTypes.EVENT.getQName())) {
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

  /**
   * A guard condition is transformed into a getter.
   *
   * @param node guard AST node
   */
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

  /**
   * An iterable of the iterate statement is transformed into a getter.
   * Currently only iterate statement of form `@iterate(Person p : persons)` is supported
   *
   * @param node iterate AST node
   */
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

  /**
   * A GUIList is transformed into an attribute, getter, and an init method.
   * A list of form `[1, 2, 3]` in a GUI model represents an immutable list that is initialized once
   * and is a static part of the component's state. For example,
   * `@PersonList(persons = [p1, p2, p3])` produces a `getPersons()` method that always returns the
   * same `List` object, which is stored as a class attribute.
   *
   * @param node GUIList AST node
   */
  @Override
  public void endVisit(ASTGUIList node) {
    // a GUIList requires preprocessing and extra information for code generation
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

  /**
   * A Call expression is transformed into either an attribute, getter, and an init method for a
   * static object, or into a function.
   * Calls of form `Person()` represent a constructor call that creates an object. The object is a
   * static part of the component's state. For example,
   * `@PersonForm(person = Person())` produces a `getPerson()` method that always returns the same
   * `Person` object, which is stored as a class attribute.
   * Calls of form `doSomething()`, `alert("my message")`, `findPerson(persons)` represent a
   * function that has to be defined by hand. The result of generation is an abstract function
   * `doSomething` whose return type is derived from the left side of the assignment.
   * Complex calls, such as `doSomething()()`, `alert(alert())`, `persons.get(calcIndex())` are
   * forbidden. Such calls must be simplified to an accepted format.
   *
   * @param node GUIList AST node
   */
  @Override
  public void endVisit(ASTCallExpression node) {
    Map<ASTNode, ASTCDAttribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();
    // a constructor call, e.g., @PersonForm(person = Person())
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
    // a custom function or a predefined one, e.g.,
    // @PersonForm(person = makePerson()) or @PersonForm(buttonClick = create(person))
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
          arguments.add(new Argument(argSTE.print(), Expression2SetterPrinter.SETTER_PARAM));
        }
        importAndPrintType(deriver.getResult());
        getCD4C().addMethod(clazz, getEmptyCallTemplate(), type, name, arguments);
      }
      else {
        Log.error("0x00014 Unable to derive the type of call "
            + util.print(node)
            + "' at " + node.get_SourcePositionStart().toString()
            + " - " + node.get_SourcePositionEnd().toString()
            + ". Please make sure that the call is simple, e.g., doStuff(),"
            + " a direct value of"
            + " a GUI component parameter, "
            + " a GUI component argument, "
            + " a guard, "
            + " an iterable item, "
            + " or is inside a block."
        );
      }
    }
  }

  /**
   * @param ast base GUI AST to create an empty class from
   * @return empty class
   */
  protected ASTCDClass emptyClass(ASTGUICompilationUnit ast) {
    ASTGUIComponentDeclaration domainClass = ast.getGUIComponentDeclaration();
    String className = domainClass.getName();
    return UMLPMill.cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(className)
        .build();
  }

  /**
   * Add directive to a Java class for further JSweet translation
   */
  protected void addDirectiveAnnotation() {
    String domainModel = util.getMainDomainModelQName().orElse("");
    boolean isEmpty = gui.getGUIComponentDeclaration().getGUIParamList().isEmpty();

    getCD4C().addImport(clazz, "umlp.jsweet.extension.annotation.Directive");
    glex.replaceTemplate("cd2java.Annotations", clazz,
        new TemplateHookPoint(getDirectiveAnnotationTemplate(), domainModel, isEmpty));
  }

  protected ASTCDMethodSignature addConstructor() {
    return getCD4C().addConstructor(clazz, "umlp.gui2java.GUI2JavaConstructor");
  }

  /**
   * An init method is a slightly more flexible alternative to a constructor.
   * @return class initialization method
   */
  protected ASTCDMethodSignature addInit() {
    return getCD4C().addMethod(clazz, "umlp.gui2java.GUI2JavaInit");
  }

  /**
   * Transform a GUI type into a Java type and add all necessary imports for its usage. For example,
   * `List<Person>` is transformed into `GemList<Person>` and imports `umlp.backendrte.GemList`,
   * `my.domain.Person` are added along the way.
   *
   * @param ste GUI type to be processed
   * @return Java type that corresponds to the input GUI type
   */
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
      // a List<Person> must be printed as GemList<Person>
      // a List<String> as a List<String>
      // a List<MyDomainEnum> as a List<MyDomainEnum>
      if (util.getDomainListInnerType(ste).isPresent()
          && !util.isEnumType(util.getDomainListInnerType(ste).get())) {
        getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("GemList"));
        StringBuilder sb = new StringBuilder("GemList");
        sb.append("<");
        Iterator<SymTypeExpression> steArgIter = stog.getArgumentList().iterator();
        while (steArgIter.hasNext()) {
          sb.append(importAndPrintType(steArgIter.next()));
          if (steArgIter.hasNext()) {
            sb.append(",");
          }
        }
        sb.append(">");
        return sb.toString();
      }
      getCD4C().addImport(clazz, stog.getTypeConstructorFullName());
      for (SymTypeExpression steArg : stog.getArgumentList()) {
        importAndPrintType(steArg);
      }
    }
    else if (ste.isPrimitive()) {
      return ste.asPrimitive().getBoxedPrimitiveName();
    }
    else if (!(ste.isNullType() || ste.isVoidType() || ste.isWildcard() || ste.isTypeVariable())) {
      Log.error("0x00010 Encountered unsupported type in " + gui.getComponentQName()
          + ": parameter type '" + ste.printFullName() + "'");
    }
    return ste.print();
  }

  protected String getDirectiveAnnotationTemplate() {
    return "umlp.gui2java.GUI2JavaDirectiveAnnotation";
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

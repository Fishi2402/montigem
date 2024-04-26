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
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
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
import de.monticore.types.mcbasictypes._ast.ASTMCType;
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

  protected List<Method> addedMethods = new ArrayList<>();

  public GUI2JavaClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    this.resetHooks();
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
  }

  protected void resetHooks() {
    glex.setAfterTemplate(getInitBodyHookpoint(), new ArrayList<>());
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
   * @param cd  output CD AST
   */
  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    this.gui = gui;
    replaceTemplates();

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

    addRouter();

    StaticObjectsCollector.getInstance().init(gui.getGUIComponentDeclaration());
    DependencyScopeTree.getInstance().init(gui.getGUIComponentDeclaration());
    gui.accept(traverser);

    // constructor
    addConstructor();
    // init
    addInit();
  }

  /**
   * Replace CD2Java generator templates with GUI2Java templates. The templates to be replaced here
   * are independent of a specific AST node. Node-dependent templates have getters of form
   * "public String get<template_name>Template()"
   */
  protected void replaceTemplates() {
    glex.replaceTemplate("cd2java.Imports", new StringHookPoint(""));
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
    String name = "_" + node.getName();

    // add attr
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), type, name);
    clazz.addCDMember(attr);

    // add getter
    String getterName = "get" + StringTransformations.capitalize(node.getName());
    getCD4C().addMethod(clazz, getParamGetterTemplate(), type, name, getterName);
    // add setter
    String setterName = "set" + StringTransformations.capitalize(node.getName());
    getCD4C().addMethod(clazz, getParamSetterTemplate(), type, name, setterName);

    String initName = "init" + StringTransformations.capitalize(node.getName());
    // case 1: component Foo(Person p)
    if (util.isDomainType(typeSTE)) {
      String manager = getAndImportManager();
      // add builder init
      ASTCDMethodSignature builderInit = getCD4C().addMethod(
          clazz, getInitParamBuilderTemplate(),
          type, name, manager, initName
      );

      // add builder init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), builderInit)
      );

      Optional<DependencyScope> enclosing = DependencyScopeTree.getInstance()
          .getEnclosingScope(node);
      enclosing.ifPresent(s -> s.addInitMethod(builderInit));
    }
    // case 2: component Foo(List<Person> p)
    else if (util.getDomainListInnerType(typeSTE).isPresent()
        && !util.isEnumType(util.getDomainListInnerType(typeSTE).get())) {
      String innerType = util.getDomainListInnerType(typeSTE).get().print();
      String manager = getAndImportManager();
      // add attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDomainListTemplate(), innerType, name, manager, initName
      );

      // add attr init to class init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getInitCallTemplate(), attrInit)
      );

      Optional<DependencyScope> enclosing = DependencyScopeTree.getInstance()
          .getEnclosingScope(node);
      enclosing.ifPresent(s -> s.addInitMethod(attrInit));
    }
    // case 3: component Foo(String s, MyDomainEnum e)
    else {
      Optional<String> value = Optional.empty();
      if (node.isPresentDefault()) {
        value = Optional.of(util.print(node.getDefault()));
      }
      // add attr init
      ASTCDMethodSignature attrInit = getCD4C().addMethod(
          clazz, getInitDefaultParamTemplate(), name, value, initName, setterName
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
      // exclude GUIViewElement
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
          + " was not processed: only short version, such as '(Type item : list)',"
          + " is supported so far");
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
    Map<ASTNode, Attribute> node2Attr = StaticObjectsCollector
        .getInstance().getNode2Attr();
    if (node2Attr.containsKey(node)) {
      // add attr
      Attribute attrWrapper = node2Attr.get(node);
      ASTCDAttribute attr = attrWrapper.getCdAttr();
      for (String qName : attrWrapper.getImportList()) {
        getCD4C().addImport(clazz, qName);
      }
      clazz.addCDMember(attr);

      // calc iterate items as additional args
      Collection<VariableSymbol> iterateArgs = attrWrapper.getIterateArgs();

      ASTMCType mcType = attrWrapper.getBaseType();
      String type = mcType.printType();
      String name = attr.getName();

      if (iterateArgs.isEmpty()) {
        // add attr init
        Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
            new IndentPrinter(), false, new ArrayList<>(), new ArrayList<>()
        );
        List<String> listItems = new ArrayList<>();
        for (ASTGUIArgValue val : node.getGUIArgValueList()) {
          listItems.add(getterPrinter.prettyprint(val));
        }

        ASTCDMethodSignature attrInit = getCD4C().addMethod(clazz, getListInitTemplate(),
            mcType, name, listItems
        );

        // add getter
        getCD4C().addMethod(clazz, getGetterTemplate(),
            type, name, attrInit);

        Optional<DependencyScope> enclosing = DependencyScopeTree.getInstance()
            .getEnclosingScope(node);
        enclosing.ifPresent(s -> s.addInitMethod(attrInit));
      }
      else {
        for (VariableSymbol vs : iterateArgs) {
          importAndPrintType(vs.getType());
        }

        // add attr init
        IterationItemsFinder finder = new IterationItemsFinder();
        Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));

        Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
            new IndentPrinter(), false, iterateItems, iterateItems
        );
        List<String> listItems = new ArrayList<>();
        for (ASTGUIArgValue val : node.getGUIArgValueList()) {
          listItems.add(getterPrinter.prettyprint(val));
        }

        ASTCDMethodSignature attrInit = getCD4C().addMethod(clazz, getStaticListInitTemplate(),
            mcType, name, listItems, iterateArgs);

        // add getter
        getCD4C().addMethod(clazz, getStaticObjectGetterTemplate(),
            type, name, attrInit, iterateArgs);

        Optional<DependencyScope> enclosing = DependencyScopeTree.getInstance()
            .getEnclosingScope(node);
        enclosing.ifPresent(s -> s.addInitMethod(attrInit));
      }
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
   * function that has to be defined by hand. The result of generation are abstract functions
   * `doSomething()`, `alert(String val)`, `findPerson(List<Person> p)`, whose return type is
   * derived from the left side of the assignment.
   * Complex calls, such as `doSomething()()`, `alert(alert())`, `persons.get(calcIndex())` are
   * forbidden. Such calls must be simplified to an accepted format.
   *
   * @param node GUIList AST node
   */
  @Override
  public void endVisit(ASTCallExpression node) {
    Map<ASTNode, Attribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();
    Map<ASTNode, Method> node2Method = StaticObjectsCollector.getInstance().getNode2Method();
    // a constructor call, e.g., @PersonForm(person = Person())
    if (node2Attr.containsKey(node)) {
      if (!node.getArguments().isEmptyExpressions()) {
        Log.error("0x00016 Constructor with arguments '" + util.print(node)
            + " at " + node.getArguments().get_SourcePositionStart().toString()
            + " - " + node.getArguments().get_SourcePositionEnd().toString()
            + ". Only constructors without arguments are supported so far. Please replace the call"
            + " via a custom or a predefined function call."
        );
      }

      // add attr
      Attribute attrWrapper = node2Attr.get(node);
      for (String qName : attrWrapper.getImportList()) {
        getCD4C().addImport(clazz, qName);
      }
      ASTCDAttribute attr = attrWrapper.getCdAttr();
      clazz.addCDMember(attr);

      // calc iterate items as additional args
      Collection<VariableSymbol> iterateArgs = attrWrapper.getIterateArgs();

      ASTMCType mcType = attrWrapper.getBaseType();
      String type = mcType.printType();
      String name = attr.getName();

      if (iterateArgs.isEmpty()) {
        String manager = getAndImportManager();

        // add builder init
        ASTCDMethodSignature builderInit = getCD4C().addMethod(
            clazz, getBuilderInitTemplate(),
            mcType, name, manager
        );

        // add getter
        getCD4C().addMethod(clazz, getGetterTemplate(),
            type, name, builderInit);

        Optional<DependencyScope> enclosing = DependencyScopeTree.getInstance()
            .getEnclosingScope(node);
        enclosing.ifPresent(s -> s.addInitMethod(builderInit));
      }
      else {
        // calc iterate items as additional args
        for (VariableSymbol vs : iterateArgs) {
          importAndPrintType(vs.getType());
        }

        String manager = getAndImportManager();

        // add builder init
        ASTCDMethodSignature builderInit = getCD4C().addMethod(
            clazz, getStaticBuilderInitTemplate(),
            mcType, name, manager, iterateArgs
        );

        // add getter
        getCD4C().addMethod(clazz, getStaticObjectGetterTemplate(),
            type, name, builderInit, iterateArgs);

        Optional<DependencyScope> enclosing = DependencyScopeTree.getInstance()
            .getEnclosingScope(node);
        enclosing.ifPresent(s -> s.addInitMethod(builderInit));
      }
    }
    // a custom function or a predefined one, e.g.,
    // @PersonForm(person = makePerson()) or @PersonForm(buttonClick = create(person))
    else if (node2Method.containsKey(node)) {
      Method method = node2Method.get(node);
      String name = method.getName();
      if (!addedMethods.contains(method)) {
        addedMethods.add(method);

        // predefined function: @PersonForm(buttonClick = create(person))
        if (PredefinedFunction.getMember(name).isPresent()) {
          // TODO AGe: check type correctness
          PredefinedFunction fun = PredefinedFunction.getMember(name).get();
          String manager = getAndImportManager();

          switch (fun) {
            case CREATE:
              if (method.getParamTypes().size() != 1
                  || !util.isDomainType(method.getParamTypes().get(0))) {
                Log.error("0x0001A Function create expects a single domain type argument"
                    + " at " + node.get_SourcePositionStart().toString()
                    + " - " + node.get_SourcePositionEnd().toString());
              }
              getCD4C().addMethod(clazz, getCreateTemplate(),
                  manager, method.getParamTypes().get(0).print());
              break;
            case DELETE:
              getCD4C().addMethod(clazz, getDeleteTemplate(), manager);
              break;
            case ASSOC_ADD:
              getCD4C().addMethod(clazz, getAssocAddTemplate(), manager);
              break;
            case ASSOC_REMOVE:
              getCD4C().addMethod(clazz, getAssocRemoveTemplate(), manager);
              break;
          }
        }
        // custom function: @PersonForm(person = makePerson())
        else {
          String type = importAndPrintType(method.getReturnType());
          List<String> argTypes = new ArrayList<>();
          for (int i = 0; i < method.getParamTypes().size(); ++i) {
            SymTypeExpression argSTE = method.getParamTypes().get(i);
            argTypes.add(importAndPrintType(argSTE));
          }

          getCD4C().addMethod(clazz, getEmptyMethodTemplate(), type, name, argTypes);
        }
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
        new TemplateHookPoint("umlp.gui2java.GUI2JavaDirectiveAnnotation", domainModel, isEmpty));
  }

  protected void addRouter() {
  }

  protected ASTCDMethodSignature addConstructor() {
    return getCD4C().addConstructor(clazz, "umlp.gui2java.GUI2JavaConstructor");
  }

  /**
   * An init method is a slightly more flexible alternative to a constructor.
   *
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

        String domain = StringTransformations.uncapitalize(scope.getName()) + ".";
        String qType = ste.asObjectType().printFullName();
        getCD4C().addImport(clazz, domainType2JavaQName(qType, domain));
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

  protected String getAndImportManager() {
    if (util.getMainDomainModelSimpleName().isEmpty() || util.getMainDomainModelQName().isEmpty()) {
      Log.error("0x00007 Could not identify the manager name: domain class diagram is missing");
    }
    // We want to import java generated somepackage.flensburg.FlensburgManager;
    // but the qualified name of domain is actually somepackage.Flensburg
    // append Manager and insert lowercase cd name (e.g., flensburg) to get the result
    String manager = ManagerDecorator.getClassName(util.getMainDomainModelSimpleName().get());
    String qManager = ManagerDecorator.getClassName(util.getMainDomainModelQName().get());
    String domain = StringTransformations.uncapitalize(util.getMainDomainModelSimpleName().get()) + ".";

    getCD4C().addImport(clazz, domainType2JavaQName(qManager, domain));
    return manager;
  }

  protected String domainType2JavaQName(String qType, String insert) {
    String res = qType;
    int lastIndex = res.lastIndexOf('.');

    if (lastIndex != -1) {
      StringBuilder sb = new StringBuilder(res);
      sb.insert(lastIndex + 1, insert);
      res = sb.toString();
    }
    else {
      res = insert + res;
    }
    return res;
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

  protected String getListInitTemplate() {
    return "umlp.gui2java.GUI2JavaListInit";
  }

  protected String getGetterTemplate() {
    return "umlp.gui2java.GUI2JavaGetter";
  }

  protected String getStaticObjectGetterTemplate() {
    return "umlp.gui2java.GUI2JavaStaticObjectGetter";
  }

  protected String getStaticListInitTemplate() {
    return "umlp.gui2java.GUI2JavaStaticListInit";
  }

  protected String getBuilderInitTemplate() {
    return "umlp.gui2java.GUI2JavaBuilderInit";
  }

  protected String getStaticBuilderInitTemplate() {
    return "umlp.gui2java.GUI2JavaStaticBuilderInit";
  }

  protected String getEmptyMethodTemplate() {
    return "umlp.gui2java.GUI2JavaEmptyMethod";
  }

  protected String getEventCallerTemplate() {
    return "umlp.gui2java.GUI2JavaEventCaller";
  }

  protected String getCreateTemplate() {
    return "umlp.gui2java.predefined.GUI2JavaCreate";
  }

  protected String getDeleteTemplate() {
    return "umlp.gui2java.predefined.GUI2JavaDelete";
  }

  protected String getAssocAddTemplate() {
    return "umlp.gui2java.predefined.GUI2JavaAssocAdd";
  }

  protected String getAssocRemoveTemplate() {
    return "umlp.gui2java.predefined.GUI2JavaAssocRemove";
  }

}

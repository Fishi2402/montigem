/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
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
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUI2ClassDecorator;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Turn a GUI AST into a CD AST containing one class. The class manages component state and
 * implements some logic, such as navigation to pages, creation of objects, etc.
 */
public class GUI2JavaClassDecorator extends GUI2ClassDecorator implements GUIDSLVisitor2, CommonExpressionsVisitor2 {

  public static final String GUARD_ARG = "condition";
  public static final String ITERATE_ARG = "iterable";

  protected GUIDSLTraverser traverser;

  protected List<Method> addedMethods = new ArrayList<>();
  protected boolean extendedDomainObserver = false;
  protected Map<String, NotifyMethodTemplate> notifyDeleted = new HashMap<>();
  protected Map<String, NotifyMethodTemplate> notifyAdded = new HashMap<>();

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
   * 4. add initialization methods
   * 5. add constructor
   * Adding members includes adding initialization functions for the attributes. These functions
   * must be called in a specific order in the class initialization method, namely in reverse.
   *
   * @param gui input GUI AST
   * @param cd  output CD AST
   */
  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    super.transform(gui, cd);

    addRouter();

    ASTGUIComponentDeclaration declaration = gui.getGUIComponentDeclaration();
    StaticObjectsCollector.getInstance().init(declaration);
    InitDependencyTree.getInstance().init(declaration);
    gui.accept(traverser);

    addInit();

    // remove
    for (NotifyMethodTemplate notifyDeleted : this.notifyDeleted.values()) {
      getCD4C().addMethod(clazz, notifyDeleted.getTemplate(), notifyDeleted.getArguments().toArray());
    }

    // add
    for (NotifyMethodTemplate notifyAdded : this.notifyAdded.values()) {
      getCD4C().addMethod(clazz, notifyAdded.getTemplate(), notifyAdded.getArguments().toArray());
    }

    addConstructor();
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
   * - init function
   * The init function does not require an input parameter and sets a default value of a parameter.
   * Input parameters representing domain objects receive a value loaded from a server.
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
    if (util.isViewElement(typeSTE)
        || util.isViewElementsCollection(typeSTE)) {
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
    if (SymTypeRelations.isBoolean(typeSTE)) {
      getterName = "is" + StringTransformations.capitalize(node.getName());
    }
    getCD4C().addMethod(clazz, getParamGetterTemplate(), type, name, getterName);
    // add setter
    String setterName = "set" + StringTransformations.capitalize(node.getName());
    getCD4C().addMethod(clazz, getParamSetterTemplate(), type, name, setterName);

    String initName = "init" + StringTransformations.capitalize(node.getName());
    // case 1: component Foo(Person p)
    if (util.isDomainType(typeSTE) && !util.isEnumType(typeSTE)) {
      String manager = getAndImportManager();

      // add builder init
      Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(node);
      if (dep.isPresent()) {
        dep.get().setInitMethod(new MethodTemplate(
            getInitParamBuilderTemplate(), type, name, manager, initName
        ));
      }
    }
    // case 2: component Foo(List<Person> p)
    else if (util.getDomainListInnerType(typeSTE).isPresent()
        && !util.isEnumType(util.getDomainListInnerType(typeSTE).get())) {
      String innerType = util.getDomainListInnerType(typeSTE).get().print();
      String manager = getAndImportManager();

      // add remove
      ASTCDMethodSignature remove = getCD4C().addMethod(
          clazz, getRemoveDomainListTemplate(), node.getName(), getterName);

      // add add
      ASTCDMethodSignature add = getCD4C().addMethod(
          clazz, getAddDomainListTemplate(), innerType, node.getName(), getterName);

      // add list item observer
      if (gui.getGUIComponentDeclaration().isPage()) {
        if (!extendedDomainObserver) {
          extendDomainObserver();
          extendedDomainObserver = true;
        }

        // TODO: simple name clash, e.g., foo.Person and bar.Person
        // add notifyDeleted method
        if (!notifyDeleted.containsKey(innerType)) {
          notifyDeleted.put(innerType,
              new NotifyMethodTemplate(getNotifyDeletedTemplate(), innerType));
        }
        notifyDeleted.get(innerType).addCalledMethod(remove.getName());

        // add notifyAdded method
        if (!notifyAdded.containsKey(innerType)) {
          notifyAdded.put(innerType,
              new NotifyMethodTemplate(getNotifyAddedTemplate(), innerType));
        }
        notifyAdded.get(innerType).addCalledMethod(add.getName());
      }

      // add attr init
      Optional<InitDependency> dep = InitDependencyTree.getInstance()
          .getDependency(node);
      if (dep.isPresent()) {
        // add init
        dep.get().setInitMethod(new MethodTemplate(
            getInitDomainListTemplate(), innerType, name, manager, initName
        ));
      }
    }
    // case 3: component Foo(String s, MyDomainEnum e)
    else {
      Optional<String> value = Optional.empty();
      if (node.isPresentDefault()) {
        value = Optional.of(util.print(node.getDefault()));
      }
      // add attr init
      Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(node);
      if (dep.isPresent()) {
        dep.get().setInitMethod(new MethodTemplate(
            getInitDefaultParamTemplate(), name, value, initName, setterName
        ));
      }
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
      if (util.isViewElement(typeSTE)
          || util.isViewElementsCollection(typeSTE)) {
        return;
      }
      String type = importAndPrintType(typeSTE);

      // calc name
      String name = node.getId().getName() + StringTransformations.capitalize(arg.getName());

      // add getter
      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
      Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, nonClassMembers
      );
      Expression2SetterPrinter setterPrinter = new Expression2MutatorPrinter(
          new IndentPrinter(), false, iterateItems, nonClassMembers
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
    Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, nonClassMembers
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
    Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, nonClassMembers
    );
    Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

    String nullCheck = nullCheckPrinter.prettyprint(control.getIterable());
    String getterValue = getterPrinter.prettyprint(control.getIterable());

    // calc iterate items as additional args
    List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(node.getEnclosingScope());
    for (VariableSymbol vs : iterateArgs) {
      importAndPrintType(vs.getType());
    }

    getCD4C().addMethod(
        clazz, getArgGetterTemplate(),
        type, name, nullCheck, getterValue, iterateArgs
    );

    // add init
    if (!control.getItem().isPresentSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + control.getItem().getName()
          + "' at " + control.getItem().get_SourcePositionStart().toString()
          + " - " + control.getItem().get_SourcePositionEnd().toString()
      );
    }
    SymTypeExpression itemTypeSTE = control.getItem().getSymbol().getType();
    String itemType = importAndPrintType(itemTypeSTE);

    Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(control);
    if (dep.isPresent()) {
      // add init
      dep.get().setInitMethod(new MethodTemplate(
          getIterateInitTemplate(), itemType, name, getterValue, iterateArgs
      ));
    }
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
      List<VariableSymbol> iterateArgs = attrWrapper.getIterateArgs();

      ASTMCType mcType = attrWrapper.getBaseType();
      String type = mcType.printType();
      String name = attr.getName();

      if (iterateArgs.isEmpty()) {
        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getGetterTemplate(), type, name);

        // add attr init
        Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
            new IndentPrinter(), false, new ArrayList<>(), new ArrayList<>()
        );
        List<String> listItems = new ArrayList<>();
        for (ASTGUIArgValue val : node.getGUIArgValueList()) {
          listItems.add(getterPrinter.prettyprint(val));
        }

        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          // add init
          dep.get().setInitMethod(new MethodTemplate(
              getListInitTemplate(), mcType, name, listItems
          ));
        }
      }
      else {
        for (VariableSymbol vs : iterateArgs) {
          importAndPrintType(vs.getType());
        }

        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getStaticObjectGetterTemplate(),
            type, name, iterateArgs);

        // add attr init
        IterationItemsFinder finder = new IterationItemsFinder();
        Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
        Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

        Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
            new IndentPrinter(), false, iterateItems, nonClassMembers
        );
        List<String> listItems = new ArrayList<>();
        for (ASTGUIArgValue val : node.getGUIArgValueList()) {
          listItems.add(getterPrinter.prettyprint(val));
        }

        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          // add init
          dep.get().setInitMethod(new MethodTemplate(
              getStaticListInitTemplate(), mcType, name, listItems, iterateArgs
          ));
        }
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
      List<VariableSymbol> iterateArgs = attrWrapper.getIterateArgs();

      ASTMCType mcType = attrWrapper.getBaseType();
      String type = mcType.printType();
      String name = attr.getName();

      if (iterateArgs.isEmpty()) {
        String manager = getAndImportManager();

        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getGetterTemplate(), type, name);

        // add builder init
        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          dep.get().setInitMethod(new MethodTemplate(
              getBuilderInitTemplate(), mcType, name, manager
          ));
        }
      }
      else {
        // calc iterate items as additional args
        for (VariableSymbol vs : iterateArgs) {
          importAndPrintType(vs.getType());
        }

        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getStaticObjectGetterTemplate(),
            type, name, iterateArgs);

        String manager = getAndImportManager();

        // add builder init
        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          dep.get().setInitMethod(new MethodTemplate(
              getStaticBuilderInitTemplate(), mcType, name, manager, iterateArgs
          ));
        }
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
                Log.error("0x0001A Function 'create' expects a single domain type argument"
                    + " at " + node.get_SourcePositionStart().toString()
                    + " - " + node.get_SourcePositionEnd().toString());
              }
              getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getMCType());
              getCD4C().addMethod(clazz, getCreateTemplate(),
                  method.getParamTypes().get(0).print());
              break;
            case DELETE:
              if (method.getParamTypes().size() != 1
                  || !util.isDomainType(method.getParamTypes().get(0))) {
                Log.error("0x0001A Function 'delete' expects a single domain type argument"
                    + " at " + node.get_SourcePositionStart().toString()
                    + " - " + node.get_SourcePositionEnd().toString());
              }
              getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getMCType());
              getCD4C().addMethod(clazz, getDeleteTemplate(),
                  method.getParamTypes().get(0).print(), manager);
              break;
            case ASSOC_ADD: {
              // TODO AGe: better "first arg is an assoc" check
              if (method.getParamTypes().size() != 2
                  || !util.isDomainListType(method.getParamTypes().get(0))
                  || node.getArguments().sizeExpressions() != 2
                  || !(node.getArguments().getExpression(0) instanceof ASTFieldAccessExpression)
                  || !util.isDomainType(method.getParamTypes().get(1))
              ) {
                Log.error("0x0001A Function 'assoc_add' expects 2 arguments:"
                    + "an association and a domain object"
                    + " at " + node.get_SourcePositionStart().toString()
                    + " - " + node.get_SourcePositionEnd().toString());
              }
              getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getMCType());
              addAssocMethod(method, node, getAssocAddTemplate());
              break;
            }
            case ASSOC_REMOVE: {
              // TODO AGe: better "first arg is an assoc" check
              if (method.getParamTypes().size() != 2
                  || !util.isDomainListType(method.getParamTypes().get(0))
                  || node.getArguments().sizeExpressions() != 2
                  || !(node.getArguments().getExpression(0) instanceof ASTFieldAccessExpression)
                  || !util.isDomainType(method.getParamTypes().get(1))
              ) {
                Log.error("0x0001A Function 'assoc_remove' expects 2 arguments:"
                    + "an association and a domain object"
                    + " at " + node.get_SourcePositionStart().toString()
                    + " - " + node.get_SourcePositionEnd().toString());
              }
              getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getMCType());
              addAssocMethod(method, node, getAssocRemoveTemplate());
              break;
            }
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

  @Override
  public void endVisit(ASTFieldAccessExpression node) {
    Map<ASTNode, Attribute> node2Attr = StaticObjectsCollector.getInstance().getNode2Attr();
    // an assoc access, e.g., @AddressForm(person = person.address)
    if (node2Attr.containsKey(node)) {
      // get init method info
      Attribute attrWrapper = node2Attr.get(node);
      for (String qName : attrWrapper.getImportList()) {
        getCD4C().addImport(clazz, qName);
      }
      ASTCDAttribute attr = attrWrapper.getCdAttr();

      // calc iterate items as additional args
      List<VariableSymbol> iterateArgs = attrWrapper.getIterateArgs();

      ASTMCType mcType = attrWrapper.getBaseType();
      String name = attr.getName();

      for (VariableSymbol vs : iterateArgs) {
        importAndPrintType(vs.getType());
      }

      // add assoc init
      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
      Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, nonClassMembers
      );
      String getter = getterPrinter.prettyprint(node);

      Optional<InitDependency> dep = InitDependencyTree.getInstance()
          .getDependency(node);
      if (dep.isPresent()) {
        dep.get().setInitMethod(new MethodTemplate(
            getAssocInitTemplate(), mcType, name, getter, iterateArgs
        ));
      }
    }
  }

  /**
   * Add directive to a Java class for further JSweet translation
   */
  protected void addClassAnnotation() {
    String domainModel = util.getMainDomainModelQName().orElse("");
    boolean isEmpty = gui.getGUIComponentDeclaration().getGUIParamList().isEmpty();

    getCD4C().addImport(clazz, "umlp.jsweet.extension.annotation.Directive");
    glex.replaceTemplate("cd2java.Annotations", clazz,
        new TemplateHookPoint("umlp.gui2java.GUI2JavaDirectiveAnnotation", domainModel, isEmpty));
  }

  protected void addConstructor() {
    getCD4C().addConstructor(clazz, "umlp.gui2java.GUI2JavaConstructor");
  }

  /**
   * An init method is a slightly more flexible alternative to a constructor.
   *
   * @return class initialization method
   */
  protected void addInit() {
    Optional<InitDependency> root = InitDependencyTree.getInstance()
        .getDependency(gui.getGUIComponentDeclaration());
    if (root.isPresent()) {
      Collection<ASTCDMethodSignature> paramInit = addInitMethods(root.get());
      for (ASTCDMethodSignature init : paramInit) {
        glex.addAfterTemplate(
            getInitBodyHookpoint(),
            new TemplateHookPoint(getInitCallTemplate(), init)
        );
      }
    }

    getCD4C().addMethod(clazz, "umlp.gui2java.GUI2JavaInit");
  }

  protected Collection<ASTCDMethodSignature> addInitMethods(InitDependency ds) {
    Collection<ASTCDMethodSignature> res = new ArrayList<>();
    if (ds.getInitMethod().isEmpty()) {
      for (InitDependency sub : ds.getDependentList()) {
        res.addAll(addInitMethods(sub));
      }
    }
    else {
      MethodTemplate initMethod = ds.getInitMethod().get();

      Collection<ASTCDMethodSignature> subInitList = new ArrayList<>();
      for (InitDependency sub : ds.getDependentList()) {
        subInitList.addAll(addInitMethods(sub));
      }
      List<Object> args = initMethod.getArguments();
      args.add(subInitList);
      res.add(getCD4C().addMethod(clazz, initMethod.getTemplate(), args.toArray()));
    }
    return res;
  }

  protected void extendDomainObserver() {
    // extend observer
    if (!clazz.isPresentCDExtendUsage()) {
      clazz.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
          .setSuperclassList(new ArrayList<>()).build());
    }
    clazz.getSuperclassList().add(MCTypeFacade.getInstance()
        .createQualifiedType(getAndImportDomainObserver()));

    // add observer init
    glex.addAfterTemplate(
        getInitBodyHookpoint(),
        new TemplateHookPoint(getAddObserverTemplate(), getAndImportManager())
    );

    // implement getScope method of observer
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addMethod(clazz, getScopeTemplate());
  }

  protected void addAssocMethod(Method method, ASTCallExpression node, String template) {
    String type = importAndPrintType(method.getParamTypes().get(0));

    ASTExpression holder = ((ASTFieldAccessExpression) node.getArguments()
        .getExpression(0)).getExpression();

    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
    Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, nonClassMembers
    );
    String getterValue = getterPrinter.prettyprint(holder);
    boolean isList = util.isDomainListType(method.getParamTypes().get(0));

    String assoc = ((ASTFieldAccessExpression) node.getArguments()
        .getExpression(0)).getName();

    getCD4C().addMethod(clazz, template,
        type, getterValue, isList, assoc);
  }

  protected String getInitDefaultParamTemplate() {
    return "umlp.gui2java.GUI2JavaInitDefaultParam";
  }

  protected String getInitDomainListTemplate() {
    return "umlp.gui2java.GUI2JavaInitDomainList";
  }

  protected String getRemoveDomainListTemplate() {
    return "umlp.gui2java.GUI2JavaRemoveDomainList";
  }

  protected String getAddDomainListTemplate() {
    return "umlp.gui2java.GUI2JavaAddDomainList";
  }

  protected String getNotifyDeletedTemplate() {
    return "umlp.gui2java.GUI2JavaNotifyDeleted";
  }

  protected String getNotifyAddedTemplate() {
    return "umlp.gui2java.GUI2JavaNotifyAdded";
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

  protected String getAddObserverTemplate() {
    return "umlp.gui2java.GUI2JavaAddObserver";
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

  protected String getIterateInitTemplate() {
    return "umlp.gui2java.GUI2JavaIterateInit";
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

  protected String getAssocInitTemplate() {
    return "umlp.gui2java.GUI2JavaAssocInit";
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

  protected String getScopeTemplate() {
    return "umlp.gui2java.GUI2JavaScope";
  }

}

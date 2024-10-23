/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
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
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.GUI2ClassDecorator;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.genjava.prettyprinter.ExpressionPrinterConfig;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.PathUtil;
import umlp.generator.util.TagLookUp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
  protected Set<String> implementedAssocObservers = new HashSet<>();
  protected Set<String> subscribedManagerEvents = new HashSet<>();
  protected Map<String, NotifyMethodTemplate> notifyDeleted = new HashMap<>();
  protected Map<String, NotifyMethodTemplate> notifyAdded = new HashMap<>();

  public GUI2JavaClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
  }

  protected void resetHooks() {
    glex.setAfterTemplate(getInitBodyHookpoint(), new ArrayList<>());
    glex.setAfterTemplate(getDestroyBodyHookpoint(), new ArrayList<>());
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
    this.resetHooks();
    super.transform(gui, cd);

    addRouter();

    ASTGUIComponentDeclaration declaration = gui.getGUIComponentDeclaration();
    StaticObjectsCollector.getInstance().init(declaration);
    InitDependencyTree.getInstance().init(declaration);
    gui.accept(traverser);

    addInit();
    addDestroy();

    getCD4C().addImport(clazz, "java.util.ArrayList");

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
    ASTMCType type = createTypeAndAddImports(typeSTE);

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

    // add init check attr
    String initCheckName = name + "Initialized";
    ASTCDAttribute initCheckAttr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), MCTypeFacade.getInstance().createBooleanType(), initCheckName,
        // initial is false
        CommonExpressionsMill.literalExpressionBuilder()
            .setLiteral(
                MCCommonLiteralsMill.booleanLiteralBuilder()
                    .setSource(ASTConstantsMCCommonLiterals.FALSE)
                    .build()
            ).build()
    );
    clazz.addCDMember(initCheckAttr);

    // add setter
    String setterName = "set" + StringTransformations.capitalize(node.getName());
    getCD4C().addMethod(clazz, getParamSetterTemplate(), type, name, setterName, initCheckName);

    String initName = "init" + StringTransformations.capitalize(node.getName());

    // case 1: component Foo(App app)
    if (typeSTE.isObjectType() && TagLookUp.isSingleton(typeSTE.getTypeInfo())) {
      // Use the manager corresponding (when importing from extended CDs)
      String manager = getAndImportManager(typeSTE.asObjectType());

      // add builder init
      Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(node);
      if (dep.isPresent()) {
        dep.get().setInitMethod(new MethodTemplate(
            getInitParamSingletonTemplate(), type, name, manager, initName, initCheckName
        ));
      }
    }
    // case 2: component Foo(Person p)
    else if (util.isDomainType(typeSTE) && !util.isEnumType(typeSTE)) {
      String manager = getAndImportManager(typeSTE.asObjectType());
      if (typeSTE.hasTypeInfo() && TagLookUp.isDerived(typeSTE.getTypeInfo())) {
        // add derived class init
        Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(node);
        if (dep.isPresent()) {
          dep.get().setInitMethod(new MethodTemplate(
              getInitDerivedDomainParamTemplate(), type, name, manager, initName, initCheckName
          ));
        }
      }
      else {
        String id = util.getRoutingId(node);
        getCD4C().addAttribute(clazz, "public Integer " + id);

        // add builder init
        Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(node);
        if (dep.isPresent()) {
          dep.get().setInitMethod(new MethodTemplate(
              getInitDomainParamTemplate(), type, name, manager, id, initName, initCheckName, util.isAbstractDomainType(typeSTE)
          ));
        }
      }
    }
    // case 3: component Foo(List<Person> p)
    else if (util.getDomainListInnerType(typeSTE).isPresent()
        && !util.isEnumType(util.getDomainListInnerType(typeSTE).get())) {
      ASTMCType innerType = createTypeAndAddImports(util.getDomainListInnerType(typeSTE).get());
      String manager = getAndImportManager(util.getDomainListInnerType(typeSTE).get().asObjectType());

      // add remove
      ASTCDMethodSignature remove = getCD4C().addMethod(
          clazz, getRemoveDomainListTemplate(), node.getName(), getterName);

      // add add
      ASTCDMethodSignature add = getCD4C().addMethod(
          clazz, getAddDomainListTemplate(), innerType, node.getName(), getterName);

      // add list item observer
      if (gui.getGUIComponentDeclaration().isPage()) {
        addDomainObserverMethods();
        if (!subscribedManagerEvents.contains(innerType.printType())) {
          subscribedManagerEvents.add(innerType.printType());
          getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));
          importCommand(innerType.printType());
          /*
            CommandManager.executeCommand(new Person_subscribeManagerEvents());
           */
          glex.addAfterTemplate(
              getInitBodyHookpoint(),
              new TemplateHookPoint(getSubscribeManagerEventsTemplate(), innerType)
          );
        }

        // TODO: simple name clash, e.g., foo.Person and bar.Person
        // add notifyDeleted method
        if (!notifyDeleted.containsKey(innerType.printType())) {
          notifyDeleted.put(innerType.printType(),
              new NotifyMethodTemplate(getNotifyDeletedTemplate(), innerType));
        }
        notifyDeleted.get(innerType.printType()).addCalledMethod(remove.getName());

        // add notifyAdded method
        if (!notifyAdded.containsKey(innerType.printType())) {
          notifyAdded.put(innerType.printType(),
              new NotifyMethodTemplate(getNotifyAddedTemplate(), innerType));
        }
        notifyAdded.get(innerType.printType()).addCalledMethod(add.getName());
      }

      // add attr init
      Optional<InitDependency> dep = InitDependencyTree.getInstance()
          .getDependency(node);
      if (dep.isPresent()) {
        // add init
        dep.get().setInitMethod(new MethodTemplate(
            getInitDomainListTemplate(), innerType, name, manager, initName, initCheckName
        ));
      }
    }
    // case 4: component Foo(String s, MyDomainEnum e)
    else {
      Optional<String> value = Optional.empty();
      if (node.isPresentDefault()) {
        value = Optional.of(util.print(node.getDefault()));
      }
      // add attr init
      Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(node);
      if (dep.isPresent()) {
        dep.get().setInitMethod(new MethodTemplate(
            getInitDefaultParamTemplate(), name, value, initName, initCheckName, getterName, setterName
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
        continue;
      }
      ASTMCType type = createTypeAndAddImports(typeSTE);

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

      List<ASTCDParameter> params = getIterationItemParameters(arg);

      if (typeSTE.printFullName().startsWith(BuiltInTypes.EVENT.getQName())) {
        ASTMCType innerType;
        if (typeSTE.isGenericType() && typeSTE.asGenericType().sizeArguments() == 1) {
          innerType = createTypeAndAddImports(typeSTE.asGenericType().getArgument(0));
        }
        else {
          if (typeSTE.isObjectType()) {
            Log.error("0x0001F Found Event -typed argument without type parameters"
                + " (underspecified Event type)."
                + " Event type expects exactly a single type parameter, e.g. Event<Integer>."
                + " At " + arg.get_SourcePositionStart().toString()
                + " - " + arg.get_SourcePositionEnd().toString()
            );
          }
          else if (typeSTE.isGenericType() && typeSTE.asGenericType().sizeArguments() != 1) {
            Log.error("0x0001F Found Event -typed argument with multiple type parameters."
                + " Event type expects exactly a single type parameter, e.g. Event<Integer>."
                + " At " + arg.get_SourcePositionStart().toString()
                + " - " + arg.get_SourcePositionEnd().toString()
            );
          }
          else {
            Log.error("0x00020 Unknown error while processing an Event -typed argument "
                + " at " + arg.get_SourcePositionStart().toString()
                + " - " + arg.get_SourcePositionEnd().toString()
            );
          }
          return;
        }

        // add event caller
        getCD4C().addMethod(
            clazz, getEventCallerTemplate(),
            innerType, name, getterValue, params
        );
      }
      else {
        String returnValue = Expression2SetterPrinter.SETTER_PARAM;
        String conv = "";
        if (arg.getGUIArgValue().isPresentExpression()) {
          // example: `String val = date2String(this.getPerson().getBirthday())`
          conv = getConvertedValueAssignment(
              printType(createTypeAndAddImports(typeSTE)),
              returnValue,
              getConversion(typeSTE, util.getType(arg.getGUIArgValue().getExpression()))
                  .apply(getterValue)
          );
        }
        else {
          conv = getConvertedValueAssignment(
              printType(createTypeAndAddImports(typeSTE)),
              returnValue,
              getterValue
          );
        }
        // add getter
        getCD4C().addMethod(
            clazz, getArgGetterWithConversionTemplate(),
            type, name, nullCheck, returnValue, conv, params
        );
      }

      if (arg.getNameSymbol().isIsReference()) {
        // add setter
        String target = "conv_" + Expression2SetterPrinter.SETTER_PARAM;
        String value = Expression2SetterPrinter.SETTER_PARAM;
        String conv = "";
        Optional<String> setterValue = Optional.ofNullable(setterPrinter.prettyprint(arg.getGUIArgValue()));
        if (this.getClass().getSimpleName().equals("GUI2JavaClassDecorator") && (setterValue.isEmpty() || "null".equals(setterValue.get()))) {
          setterValue = Optional.of(value + " = " + value);
        }
        if (arg.getGUIArgValue().isPresentExpression()) {
          SymTypeExpression left = util.getType(arg.getGUIArgValue().getExpression());
          String convType;
          if (left.isNullType()) {
            convType = "Object";
          }
          else {
            convType = printType(createTypeAndAddImports(left));
          }
          // example: `Date val_$ = string2Date(val)`
          conv = getConvertedValueAssignment(
              convType, value, getConversion(left, typeSTE).apply(target));
        }
        getCD4C().addMethod(
            clazz, getArgSetterWithConversionTemplate(),
            type, name, nullCheck, setterValue.orElse(null), conv, params
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
    ASTMCType type;
    // if the condition is a custom method
    if (StaticObjectsCollector.getInstance().getNode2Method().containsKey(node.getCondition())) {
      Method m = StaticObjectsCollector.getInstance().getNode2Method().get(node.getCondition());
      type = createTypeAndAddImports(m.getReturnType());
    }
    else {
      SymTypeExpression typeSTE = util.getType(node.getCondition());
      type = createTypeAndAddImports(typeSTE);
    }

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

    List<ASTCDParameter> params = getIterationItemParameters(node);

    // add getter
    getCD4C().addMethod(
        clazz, getArgGetterTemplate(),
        type, name, nullCheck, getterValue, params
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
    ASTMCType type;
    // if the iterable is a custom method
    if (StaticObjectsCollector.getInstance().getNode2Method().containsKey(control.getIterable())) {
      Method m = StaticObjectsCollector.getInstance().getNode2Method().get(control.getIterable());
      type = createTypeAndAddImports(m.getReturnType());
    }
    else {
      SymTypeExpression typeSTE = util.getType(control.getIterable());
      type = createTypeAndAddImports(typeSTE);
    }

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

    List<ASTCDParameter> params = getIterationItemParameters(node);

    getCD4C().addMethod(
        clazz, getArgGetterTemplate(),
        type, name, nullCheck, getterValue, params
    );

    // add init
    if (!control.getItem().isPresentSymbol()) {
      Log.error("0x0000E Unable to find symbol for '" + control.getItem().getName()
          + "' at " + control.getItem().get_SourcePositionStart().toString()
          + " - " + control.getItem().get_SourcePositionEnd().toString()
      );
    }
    SymTypeExpression itemTypeSTE = control.getItem().getSymbol().getType();
    ASTMCType itemType = createTypeAndAddImports(itemTypeSTE);
    String itemName = control.getItem().getSymbol().getName();

    Optional<InitDependency> dep = InitDependencyTree.getInstance().getDependency(control);
    if (dep.isPresent()) {
      // add init
      dep.get().setInitMethod(new MethodTemplate(
          getIterateInitTemplate(), itemType, name, itemName, getterValue, params
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
      List<ASTCDParameter> params = attrWrapper.getParams();

      ASTMCType type = attrWrapper.getBaseType();
      String name = attr.getName();

      if (params.isEmpty()) {
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
          if (TypeScriptTypes.ARRAY_LIST.getJavaQName().isPresent()) {
            getCD4C().addImport(clazz, TypeScriptTypes.ARRAY_LIST.getJavaQName().get());
          }
          // add init
          dep.get().setInitMethod(new MethodTemplate(
              getListInitTemplate(), type, name, listItems
          ));
        }
      }
      else {
        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getStaticObjectGetterTemplate(),
            type, name, params);

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
          if (TypeScriptTypes.HASH_MAP.getJavaQName().isPresent()) {
            getCD4C().addImport(clazz, TypeScriptTypes.HASH_MAP.getJavaQName().get());
          }
          // add init
          dep.get().setInitMethod(new MethodTemplate(
              getStaticListInitTemplate(), type, name, listItems, params
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
        SymTypeExpression ste = util.getType(node);
        if (!(ste.isObjectType() || ste.isGenericType())) {
          Log.error("0x00016 Constructor with arguments '" + util.print(node)
              + "' at " + node.getArguments().get_SourcePositionStart().toString()
              + " - " + node.getArguments().get_SourcePositionEnd().toString()
              + ". Only constructors explicitly declared with matching parameters are supported"
              + " so far. Please replace the call via a custom or a predefined function call,"
              + " e.g. get" + util.print(node)
          );
        }
      }

      // add attr
      Attribute attrWrapper = node2Attr.get(node);
      for (String qName : attrWrapper.getImportList()) {
        getCD4C().addImport(clazz, qName);
      }
      ASTCDAttribute attr = attrWrapper.getCdAttr();
      clazz.addCDMember(attr);

      // calc iterate items as additional args
      List<ASTCDParameter> params = attrWrapper.getParams();

      ASTMCType type = attrWrapper.getBaseType();
      String name = attr.getName();

      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
      Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, nonClassMembers
      );
      List<String> constructorArgs = new ArrayList<>();
      for (ASTExpression val : node.getArguments().getExpressionList()) {
        constructorArgs.add(getterPrinter.prettyprint(val));
      }

      if (params.isEmpty()) {
        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getGetterTemplate(), type, name);

        // add builder init
        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          if (constructorArgs.isEmpty()) {
            String manager = getAndImportManager();

            dep.get().setInitMethod(new MethodTemplate(
                getBuilderInitTemplate(), type, name, manager
            ));
          }
          else {
            dep.get().setInitMethod(new MethodTemplate(
                getConstructorInitTemplate(), type, name, constructorArgs
            ));
          }
        }
      }
      else {
        // add getter
        ASTCDMethodSignature getter = getCD4C().addMethod(clazz, getStaticObjectGetterTemplate(),
            type, name, params);

        // add builder init
        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          if (TypeScriptTypes.HASH_MAP.getJavaQName().isPresent()) {
            getCD4C().addImport(clazz, TypeScriptTypes.HASH_MAP.getJavaQName().get());
          }
          if (constructorArgs.isEmpty()) {
            String manager = getAndImportManager();

            dep.get().setInitMethod(new MethodTemplate(
                getStaticBuilderInitTemplate(), type, name, manager, params
            ));
          }
          else {
            dep.get().setInitMethod(new MethodTemplate(
                getStaticConstructorInitTemplate(), type, name, constructorArgs, params
            ));
          }
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
              if (TypeScriptTypes.CONSUMER.getJavaQName().isPresent()) {
                getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getJavaQName().get());
              }
              getCD4C().addMethod(clazz, getCreateTemplate(),
                  method.getAlias(), createTypeAndAddImports(method.getParamTypes().get(0)));
              break;
            case DELETE:
              if (method.getParamTypes().size() != 1
                  || !util.isDomainType(method.getParamTypes().get(0))) {
                Log.error("0x0001A Function 'delete' expects a single domain type argument"
                    + " at " + node.get_SourcePositionStart().toString()
                    + " - " + node.get_SourcePositionEnd().toString());
              }
              if (TypeScriptTypes.CONSUMER.getJavaQName().isPresent()) {
                getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getJavaQName().get());
              }
              getCD4C().addMethod(clazz, getDeleteTemplate(),
                  method.getAlias(), createTypeAndAddImports(method.getParamTypes().get(0)), manager);
              break;
            case ASSOC_ADD: {
              addAssocListMethod(method, node, getAssocAddTemplate());
              break;
            }
            case ASSOC_REMOVE: {
              addAssocListMethod(method, node, getAssocRemoveTemplate());
              break;
            }
            case ASSOC_SET: {
              addAssocListMethod(method, node, getAssocSetTemplate());
              break;
            }
            case ASSOC_SET_ABSENT: {
              addAssocMethod(method, node, getAssocSetAbsentTemplate());
              break;
            }
          }
        }
        // custom function: @PersonForm(person = makePerson())
        else {
          ASTMCType type = createTypeAndAddImports(method.getReturnType());
          List<ASTCDParameter> params = new ArrayList<>();
          for (int i = 0; i < method.getParamTypes().size(); ++i) {
            SymTypeExpression argSTE = method.getParamTypes().get(i);
            params.add(CDParameterFacade.getInstance().createParameter(
                createTypeAndAddImports(argSTE),
                Expression2SetterPrinter.SETTER_PARAM + "_$" + i
            ));
          }

          getCD4C().addMethod(clazz, getEmptyMethodTemplate(), type, name, params);
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
      List<ASTCDParameter> params = attrWrapper.getParams();

      String name = attr.getName();

      SymTypeExpression accessType = util.getType(node);
      SymTypeExpression ownerType = util.getType(node.getExpression());
      // a domain list, e.g., @FriendsOverview(persons = person.friends)
      if (util.isDomainListType(accessType)) {
        // add assoc init
        if (ownerType.hasTypeInfo() && TagLookUp.isDerived(ownerType.getTypeInfo())) {
          ExpressionPrinterConfig.getInstance().customConversions.put(node,
              n -> "calc" + StringTransformations.capitalize(node.getName()) + "()");
        }
        else {
          String tmpName = ExpressionPrinterConfig.attr2Getter(node);
          if (tmpName.endsWith("List")) {
            tmpName = tmpName.substring(0, tmpName.length() - 4) + "GemList";
          }
          final String exprName = tmpName;
          ExpressionPrinterConfig.getInstance().customConversions.put(node,
              n -> "_internal_" + exprName + "().loadAll()");

          // extends PersonObserverMixin
          addAssocObserverMethods(ownerType, node);
        }

        IterationItemsFinder finder = new IterationItemsFinder();
        Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
        Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

        Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
            new IndentPrinter(), false, iterateItems, nonClassMembers
        );
        String getter = getterPrinter.prettyprint(node);
        ExpressionPrinterConfig.getInstance().customConversions.remove(node);
        String ownerGetter = getterPrinter.prettyprint(node.getExpression());

        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          dep.get().setInitMethod(new MethodTemplate(
              getListAssocInitTemplate(), name, getter, ownerGetter, params
          ));
        }
      }
      // a domain object, e.g., @AddressForm(person = person.address)
      else if (util.isDomainType(accessType)) {
        // add assoc init
        IterationItemsFinder finder = new IterationItemsFinder();

        if (ownerType.hasTypeInfo() && TagLookUp.isDerived(ownerType.getTypeInfo())) {
          ExpressionPrinterConfig.getInstance().customConversions.put(node,
              n -> "calc" + StringTransformations.capitalize(node.getName()) + "()");
        }
        else {
          ExpressionPrinterConfig.getInstance().customConversions.put(node,
              n -> ExpressionPrinterConfig.attr2Getter(node) + "Gem().load()");
        }
        Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
        Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

        Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
            new IndentPrinter(), false, iterateItems, nonClassMembers
        );
        String getter = getterPrinter.prettyprint(node);
        ExpressionPrinterConfig.getInstance().customConversions.remove(node);

        Optional<InitDependency> dep = InitDependencyTree.getInstance()
            .getDependency(node);
        if (dep.isPresent()) {
          dep.get().setInitMethod(new MethodTemplate(
              getObjectAssocInitTemplate(), name, getter, params
          ));
        }
      }
    }
    else if (util.isEnumMember(node)) {
      createTypeAndAddImports(util.getType(node));
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

    getCD4C().addMethod(clazz, getInitTemplate());
  }

  @Override
  protected void addDestroy() {
    getCD4C().addMethod(clazz, getDestroyTemplate());
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
  }

  protected void addDomainObserverMethods() {
    if (!extendedDomainObserver) {
      extendDomainObserver();
      extendedDomainObserver = true;
      // add observer init
      glex.addAfterTemplate(
          getInitBodyHookpoint(),
          new TemplateHookPoint(getAddObserverTemplate(), getAndImportManager())
      );

      // add observer destroy
      glex.addAfterTemplate(
          getDestroyBodyHookpoint(),
          new TemplateHookPoint(getRemoveObserverTemplate(), getAndImportManager())
      );

      // implement getScope method of observer
      getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
      getCD4C().addMethod(clazz, getScopeTemplate());
    }
  }

  protected void implementAssocObserver(SymTypeExpression ownerType, ASTFieldAccessExpression assoc) {
    // the CD Generator produces `hardy.domain.Person`, instead of `hardy.Domain.Person`, calculate
    String qType = ownerType.printFullName();
    String type = ownerType.print();
    List<String> parts = new ArrayList<>(Splitters.DOT.splitToList(qType));
    if (parts.size() > 1) {
      String domainPackage = parts.get(parts.size() - 2).toLowerCase();
      parts.set(parts.size() - 2, domainPackage);
      qType = Joiners.DOT.join(parts);
    }

    String qObserver = qType + ObserverDecorator.SUFFIX;
    String observer = type + ObserverDecorator.SUFFIX;
    getCD4C().addImport(clazz, qObserver);
    // implement observer
    if (!clazz.isPresentCDInterfaceUsage()) {
      clazz.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder()
          .setInterfaceList(new ArrayList<>()).build());
    }
    clazz.getInterfaceList().add(MCTypeFacade.getInstance()
        .createQualifiedType(observer));
  }

  protected void addAssocObserverMethods(SymTypeExpression ownerType, ASTFieldAccessExpression assoc) {
    if (!implementedAssocObservers.contains(ownerType.printFullName())) {
      implementAssocObserver(ownerType, assoc);
      ASTMCType type = createTypeAndAddImports(ownerType);
      // add observer init
      getCD4C().addMethod(clazz, getNotifyAssocAddedTemplate(),
          type, assoc.getName());
      implementedAssocObservers.add(ownerType.printFullName());
    }
  }

  protected void addAssocListMethod(Method method, ASTCallExpression node, String template) {
    // TODO AGe: better "first arg is an assoc" check
    if (method.getParamTypes().size() != 2
        || (!util.isDomainListType(method.getParamTypes().get(0)) && !util.isDomainType(method.getParamTypes().get(0)))
        || node.getArguments().sizeExpressions() != 2
        || !(node.getArguments().getExpression(0) instanceof ASTFieldAccessExpression)
        || !util.isDomainType(method.getParamTypes().get(1))
    ) {
      Log.error("0x0001A Functions 'assoc_add' and 'assoc_remove' expect 2 arguments:"
          + "an association and a domain object"
          + " at " + node.get_SourcePositionStart().toString()
          + " - " + node.get_SourcePositionEnd().toString());
    }
    ASTMCType type = createTypeAndAddImports(method.getParamTypes().get(1));

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

    if (TypeScriptTypes.CONSUMER.getJavaQName().isPresent()) {
      getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getJavaQName().get());
    }
    getCD4C().addMethod(clazz, template,
        method.getAlias(), type, getterValue, isList, assoc);
  }

  protected void addAssocMethod(Method method, ASTCallExpression node, String template) {
    if (method.getParamTypes().size() != 1
        || node.getArguments().sizeExpressions() != 1
        || !(node.getArguments().getExpression(0) instanceof ASTFieldAccessExpression)
        || !util.isDomainType(method.getParamTypes().get(0))
    ) {
      Log.error("0x0001A Functions 'assoc_add' and 'assoc_remove' expect 2 arguments:"
          + "an association and a domain object"
          + " at " + node.get_SourcePositionStart().toString()
          + " - " + node.get_SourcePositionEnd().toString());
    }
    ASTMCType type = createTypeAndAddImports(method.getParamTypes().get(0));

    ASTExpression holder = ((ASTFieldAccessExpression) node.getArguments()
        .getExpression(0)).getExpression();

    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(gui));
    Collection<ASTExpression> nonClassMembers = new HashSet<>(iterateItems);

    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, nonClassMembers
    );
    String getterValue = getterPrinter.prettyprint(holder);
    boolean isList = false;

    String assoc = ((ASTFieldAccessExpression) node.getArguments()
        .getExpression(0)).getName();

    if (TypeScriptTypes.CONSUMER.getJavaQName().isPresent()) {
      getCD4C().addImport(clazz, TypeScriptTypes.CONSUMER.getJavaQName().get());
    }
    getCD4C().addMethod(clazz, template,
        method.getAlias(), type, getterValue, isList, assoc);
  }

  public List<ASTCDParameter> getIterationItemParameters(ASTNode node) {
    List<VariableSymbol> iterateArgs = util.findIterationItemSymbols(node.getEnclosingScope());
    List<ASTCDParameter> params = new ArrayList<>();
    for (VariableSymbol vs : iterateArgs) {
      ASTMCType paramType = createTypeAndAddImports(vs.getType());
      params.add(CDParameterFacade.getInstance().createParameter(paramType, vs.getName()));
    }
    return params;
  }

  protected String getConvertedValueAssignment(String type, String varName, String value) {
    return type + " " + varName + " = " + value;
  }

  // TODO AGe: replace with RTE functions and specific components
  public Function<String, String> getConversion(
      SymTypeExpression left, SymTypeExpression right
  ) {
    boolean leftIsString = SymTypeRelations.isString(left);

    boolean rightIsOptional = util.isOptional(right);
    if (rightIsOptional) {
      return optional2XConversion(getConversion(left, util.getOptionalTypeArgument(right)));
    }

    boolean rightIsDate = right.printFullName()
        .equals(TypeScriptTypes.LOCAL_DATE.getJavaQName().get());
    if (leftIsString && rightIsDate) {
      return date2StringConversion();
    }

    boolean rightIsDateTime = right.printFullName()
        .equals(TypeScriptTypes.LOCAL_DATE_TIME.getJavaQName().get());
    if (leftIsString && rightIsDateTime) {
      return dateTime2StringConversion();
    }

    boolean rightIsNumber = TypeScriptTypes.isNumber(right);
    if (leftIsString && rightIsNumber) {
      return number2StringConversion();
    }

    boolean rightIsEnum = util.isEnumType(right);
    if (leftIsString && rightIsEnum) {
      String type = createTypeAndAddImports(right).printType();
      return enum2StringConversion(type);
    }

    boolean rightIsString = SymTypeRelations.isString(right);

    boolean leftIsOptional = util.isOptional(left);
    if (leftIsOptional) {
      return x2OptionalConversion(getConversion(util.getOptionalTypeArgument(left), right));
    }

    boolean leftIsDate = left.printFullName()
        .equals(TypeScriptTypes.LOCAL_DATE.getJavaQName().get());
    if (rightIsString && leftIsDate) {
      createTypeAndAddImports(left);
      return string2DateConversion();
    }

    boolean leftIsDateTime = left.printFullName()
        .equals(TypeScriptTypes.LOCAL_DATE_TIME.getJavaQName().get());
    if (rightIsString && leftIsDateTime) {
      createTypeAndAddImports(left);
      return string2DateTimeConversion();
    }

    boolean leftIsNumber = TypeScriptTypes.isNumber(left);
    if (rightIsString && leftIsNumber) {
      String type = createTypeAndAddImports(left).printType();
      return string2NumberConversion(type);
    }

    boolean leftIsEnum = util.isEnumType(left);
    if (rightIsString && leftIsEnum) {
      String type = createTypeAndAddImports(left).printType();
      return string2EnumConversion(type);
    }

    return s -> s;
  }

  protected Function<String, String> date2StringConversion() {
    return s -> s + ".toString()";
  }

  protected Function<String, String> string2DateConversion() {
    return s -> "LocalDate.parse(" + s + ")";
  }

  protected Function<String, String> dateTime2StringConversion() {
    return s -> s + ".toString()";
  }

  protected Function<String, String> string2DateTimeConversion() {
    return s -> "LocalDateTime.parse(" + s + ")";
  }

  protected Function<String, String> number2StringConversion() {
    return s -> "String.valueOf(" + s + ")";
  }

  protected Function<String, String> string2NumberConversion(String type) {
    String finalType = util.getBoxedType(type);
    return s -> finalType + ".valueOf(" + s + ")";
  }

  protected Function<String, String> enum2StringConversion(String type) {
    return s -> s + ".toString()";
  }

  protected Function<String, String> string2EnumConversion(String type) {
    return s -> type + ".valueOf(" + s + ")";
  }

  /*
  protected Function<String, String> optionalNumber2StringConversion() {
    return s -> s + ".isPresent() ? " + "String.valueOf(" + s + ".get()" + ")" + " : \"\"";
  }

  protected Function<String, String> string2OptionalNumberConversion(String type) {
    String boxedType = util.getBoxedType(type);
    return s -> "Optional.of(" + boxedType + ".valueOf(" + s + ")" + ")";
  }

  protected Function<String, String> optionalEnum2StringConversion(String type) {
    return s -> s + ".isPresent() ? " + s + ".get().toString() : \"\"";
  }

  protected Function<String, String> string2OptionalEnumConversion(String type) {
    return s -> "Optional.of(" + type + ".valueOf(" + s + ")" + ")";
  }
  /**/

  protected Function<String, String> optional2XConversion(Function<String, String> conv) {
    return s -> s + ".isPresent() ? " + conv.apply(s + ".get()") + " : null";
  }

  protected Function<String, String> x2OptionalConversion(Function<String, String> conv) {
    return s -> "Optional.of(" + conv.apply(s) + ")";
  }

  protected String printType(ASTMCType type) {
    return new CdUtilsPrinter().printType(type);
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

  protected String getNotifyAssocAddedTemplate() {
    return "umlp.gui2java.GUI2JavaNotifyAssocAdded";
  }

  protected String getInitParamSingletonTemplate() {
    return "umlp.gui2java.GUI2JavaInitParamSingleton";
  }

  protected String getInitDerivedDomainParamTemplate() {
    return "umlp.gui2java.GUI2JavaInitDerivedDomainParam";
  }

  protected String getInitDomainParamTemplate() {
    return "umlp.gui2java.GUI2JavaInitDomainParam";
  }

  protected String getInitCallTemplate() {
    return "umlp.gui2java.GUI2JavaInitCall";
  }

  protected String getInitTemplate() {
    return "umlp.gui2java.GUI2JavaInit";
  }

  protected String getInitBodyHookpoint() {
    return "umlp.gui2java.GUI2JavaInitBody";
  }

  protected String getDestroyTemplate() {
    return "umlp.gui2java.GUI2JavaDestroy";
  }

  protected String getDestroyBodyHookpoint() {
    return "umlp.gui2java.GUI2JavaDestroyBody";
  }

  protected String getSubscribeManagerEventsTemplate() {
    return "umlp.gui2java.GUI2JavaSubscribeManagerEvents";
  }

  protected String getAddObserverTemplate() {
    return "umlp.gui2java.GUI2JavaAddObserver";
  }

  protected String getRemoveObserverTemplate() {
    return "umlp.gui2java.GUI2JavaRemoveObserver";
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

  protected String getArgGetterWithConversionTemplate() {
    return "umlp.gui2java.GUI2JavaArgGetterWithConversion";
  }

  protected String getArgSetterWithConversionTemplate() {
    return "umlp.gui2java.GUI2JavaArgSetterWithConversion";
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

  protected String getConstructorInitTemplate() {
    return "umlp.gui2java.GUI2JavaConstructorInit";
  }

  protected String getStaticBuilderInitTemplate() {
    return "umlp.gui2java.GUI2JavaStaticBuilderInit";
  }

  protected String getStaticConstructorInitTemplate() {
    return "umlp.gui2java.GUI2JavaStaticConstructorInit";
  }

  protected String getListAssocInitTemplate() {
    return "umlp.gui2java.GUI2JavaListAssocInit";
  }

  protected String getObjectAssocInitTemplate() {
    return "umlp.gui2java.GUI2JavaObjectAssocInit";
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

  protected String getAssocSetTemplate() {
    return "umlp.gui2java.predefined.GUI2JavaAssocAdd";
  }

  protected String getAssocSetAbsentTemplate() {
    return "umlp.gui2java.predefined.GUI2JavaAssocRemove";
  }

  protected String getScopeTemplate() {
    return "umlp.gui2java.GUI2JavaScope";
  }
}

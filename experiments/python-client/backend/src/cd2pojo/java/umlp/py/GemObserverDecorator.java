package umlp.py;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd.facade.CDTypeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.py.utily.Pyutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GemObserverDecorator extends AbstractDecorator<AbstractDecorator.NoData>
    implements CDBasisVisitor2 {

  public static final String SUFFIX = "Observer";

  protected Stack<ASTCDClass> observerClassStack = new Stack<>();
  protected Stack<String> classNameStack = new Stack<>();

  protected String domainName;

  @Override
  public void visit(ASTCDDefinition node) {
    this.domainName = node.getName();
  }

  @Override
  public void visit(ASTCDClass node) {
    // TODO: check if should decorate
    var decNode = decoratorData.getAsDecorated(node);

    var observerAttr = CDAttributeFacade.getInstance()
        .createAttribute(CDBasisMill.modifierBuilder().build(), MCTypeFacade.getInstance()
                             .createBasicGenericTypeOf("typing.List", domainName+"." + node.getName() + SUFFIX),
                         "_observer");
    CD4C.getInstance().addImport(decNode, domainName+"." + node.getName() + SUFFIX);
    var xxInstance = CD4CodeMill.fieldAccessExpressionBuilder().setExpression(
        CD4CodeMill.nameExpressionBuilder().setName(
                domainName+"." + node.getName() + GemCommandObserverDecorator.SUFFIX + "." + node.getName() + GemCommandObserverDecorator.SUFFIX)
            .build()).setName("INSTANCE").build();
//    observerAttr.setInitial(CommonExpressionsMill.callExpressionBuilder().setExpression(
//            CommonExpressionsMill.nameExpressionBuilder().setName("list").build())
//                                .setArguments(
//                                    CommonExpressionsMill.argumentsBuilder()
//                                        .addExpression(xxInstance).build())
//                                .build());
    observerAttr.setInitial(CommonExpressionsMill.arrayAccessExpressionBuilder().setExpression(
            CommonExpressionsMill.nameExpressionBuilder().setName("").build())
                                .setIndexExpression(
                                    xxInstance)
                                .build());
    CD4C.getInstance()
        .addImport(decNode, domainName+"." + node.getName() + GemCommandObserverDecorator.SUFFIX);
    // TODO: Move the CommandObserver.INSTANCE to the umlp.py.GemCommandObserverDecorator
    addToClass(decNode, observerAttr);

    var observerClass = CDTypeFacade.getInstance().createCDClass(node.getName() + SUFFIX);

    observerClassStack.add(observerClass);
    classNameStack.add(node.getName());

    addElementToParent(decoratorData.getAsDecorated(decoratorData.getParent(node).get()),
                       observerClass);


    var getScopeFunction = CDMethodFacade.getInstance()
        .createMethod(CDBasisMill.modifierBuilder().build(), CdPyUtilsPrinter.NotificationScope,
                      "getScope");
    observerClass.addCDMember(getScopeFunction);

//    GlobalExtensionManagement glex = glexOpt.get();


  }

  @Override
  public void visit(ASTCDAttribute node) {
    if (observerClassStack.isEmpty()) return;
    var info = UmlpAttrHelper.getFromSymTypeExpr(node.getSymbol().getType());

    var observerDecClass = observerClassStack.peek();

    // TODO: hook into setter

    switch (info.getMultiplicity()) {
      case MANDATORY:
        case OPTIONAL:
        handleMandatory(node, observerDecClass, info);

        break;
      case SET:
        handleSet(node, observerDecClass, info);
        break;
      default:
        Log.warn("Unknown multiplicity: " + info.getMultiplicity(), node.get_SourcePositionStart());
    }

  }

  protected void handleSet(ASTCDAttribute node, ASTCDClass observerDecClass,
                           UmlpAttrHelper.AttrData info) {
    String addMethodName = "Add" + node.getName();
    var add_set = CDMethodFacade.getInstance()
        .createMethod(CDBasisMill.modifierBuilder().PUBLIC().build(), "notify" + addMethodName,
                      CDParameterFacade.getInstance().createParameter("typing.Any",
                                                                      StringTransformations.uncapitalize(
                                                                          classNameStack.peek())),
                      CDParameterFacade.getInstance()
                          .createParameter("montigem_runtime.gembase.GEM_ID", node.getName()),
                      CDParameterFacade.getInstance().createParameter("int", "index_in_list")
                     );
    Pyutils.markAsAsync(add_set);
    addToClass(observerDecClass, add_set);
    addToClass(observerDecClass, createMaybeNotifyMethod(add_set));

    String remMethodName = "Remove" + node.getName();
    var rem_set = CDMethodFacade.getInstance()
        .createMethod(CDBasisMill.modifierBuilder().PUBLIC().build(), "notify" + remMethodName,
            CDParameterFacade.getInstance().createParameter("typing.Any",
                StringTransformations.uncapitalize(
                    classNameStack.peek())),
            CDParameterFacade.getInstance().createParameter("typing.Any", node.getName()),

            CDParameterFacade.getInstance().createParameter("int", "index_in_list")
        );
    Pyutils.markAsAsync(rem_set);
    addToClass(observerDecClass, rem_set);
    addToClass(observerDecClass, createMaybeNotifyMethod(rem_set));

    for (var setter : getData(GemSetterDecorator.class).getMethods(node)) {
      if (setter.getKind() == GemSetterDecorator.SetterMethodKind.ADD_LIST) {
        glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", setter.getSetMethod(),
                                                        new TemplateHookPoint(
                                                            "cd2python.gemobserver.CallListAdd",
                                                            addMethodName,
                                                            StringTransformations.uncapitalize(
                                                                classNameStack.peek()),
                                                            setter.getParamName())));
      } else if (setter.getKind() == GemSetterDecorator.SetterMethodKind.REM_LIST) {
        glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", setter.getSetMethod(),
            new TemplateHookPoint(
                "cd2python.gemobserver.CallListRem",
                remMethodName,
                StringTransformations.uncapitalize(
                    classNameStack.peek()),
                setter.getParamName())));
      }

    }
  }

  protected void handleMandatory(ASTCDAttribute node, ASTCDClass observerDecClass,
                                 UmlpAttrHelper.AttrData info) {
    String methodName = "Set" + node.getName();
    var notify = CDMethodFacade.getInstance()
        .createMethod(CDBasisMill.modifierBuilder().PUBLIC().build(), "notify" + methodName,
                      CDParameterFacade.getInstance().createParameter("typing.Any",
                                                                      StringTransformations.uncapitalize(
                                                                          classNameStack.peek())),
                      CDParameterFacade.getInstance().createParameter("typing.Any", "_old_value"),
                      CDParameterFacade.getInstance()
                          .createParameter("typing.Any", node.getName()));
    Pyutils.markAsAsync(notify);
    addToClass(observerDecClass, notify);

    var maybe = createMaybeNotifyMethod(notify);
    addToClass(observerDecClass, maybe);


    for (var setter : getData(GemSetterDecorator.class).getMethods(node)) {
      if (info.getTypeClass() == UmlpAttrHelper.RealType.BUILD_IN ||
          info.getTypeClass() == UmlpAttrHelper.RealType.ENUM) {
        glexOpt.ifPresent(glex -> glex.addBeforeTemplate("Setter:Before", setter.getSetMethod(),
                                                         new StringHookPoint(
                                                             "_old_value = self." + setter.getParamName())));
      } else if (info.getTypeClass() == UmlpAttrHelper.RealType.GEM) {
        glexOpt.ifPresent(glex -> glex.addBeforeTemplate("Setter:Before", setter.getSetMethod(),
                                                         new StringHookPoint(
                                                             "_old_value = self." + setter.getParamName() + ".gemId")));
      }
      glexOpt.ifPresent(glex -> glex.addAfterTemplate("Setter:After", setter.getSetMethod(),
                                                      new TemplateHookPoint(
                                                          "cd2python.gemobserver.CallMandatory",
                                                          methodName,
                                                          StringTransformations.uncapitalize(
                                                              classNameStack.peek()),
                                                          setter.getParamName())));
    }
  }

  protected ASTCDMethod createMaybeNotifyMethod(ASTCDMethod notifyMethod) {
    var maybe = CDMethodFacade.getInstance()
        .createMethod(CDBasisMill.modifierBuilder().PUBLIC().build(),
                      "maybe" + StringTransformations.capitalize(notifyMethod.getName()));
    List<String> params = new ArrayList<>();
    for (var param : notifyMethod.getCDParameterList()) {
      maybe.addCDParameter(param.deepClone());
      params.add(param.getName());
    }
    maybe.addCDParameter(CDParameterFacade.getInstance()
                             .createParameter(CdPyUtilsPrinter.NotificationScope, "scope"));
    Pyutils.markAsAsync(maybe);
    glexOpt.ifPresent(glex -> glex.replaceTemplate(Pyutils.EMPTY_TEMPLATE, maybe,
                                                   new TemplateHookPoint( // a, _old_value, myStr
                                                                          "cd2python.gemobserver.MaybeNotify",
                                                                          notifyMethod.getName(),
                                                                          params)));
    return maybe;
  }

  @Override
  public void endVisit(ASTCDClass node) {
    classNameStack.pop();
    observerClassStack.pop();
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }

  @Override
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    // TODO: Setter
    return Iterables.concat(super.getMustRunAfter(), Arrays.asList(GemSetterDecorator.class));
  }

  // TODO: Backport
  public <D> D getData(Class<? extends IDecorator<D>> decorator) {
    return  Preconditions.checkNotNull(this.decoratorData.getData(decorator), "No data for " + decorator);
  }
}

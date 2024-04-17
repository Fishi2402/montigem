/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl.types.check.util.GUIDSLTypeTraverserFactory;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.Type4Ast;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class EventDecorator extends GUI2JavaComponentDecorator {

  public EventDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst,
      GUIComponentsVisitor viewComponent) {
    super(glex, guiAst, viewComponent);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    for (ASTGUINamedArg event : util.getEventArguments(guiComponent)) {
      // TODO AGe: remove restrictions
      if (event.getGUIArgValue().isPresentExpression()) {
        if (event.getGUIArgValue().getExpression() instanceof ASTNameExpression) {
          String methodName = ((ASTNameExpression) event.getGUIArgValue().getExpression()).getName();

          getCD4C().addMethod(decoratedClass, getEventTemplate(),
              util.gui2cdEventCallName(guiComponent, event.getName()), methodName);

          Optional<ASTCDMethodSignature> methodSignature = getCD4C().createMethod(decoratedClass, getEventTemplate(), methodName, "");
          if (methodSignature.isPresent() && !GenService.containsMethod(decoratedClass, methodSignature.get())) {
            getCD4C().addMethod(decoratedClass, getEventTemplate(), methodName, "");
          }
        }
        if (event.getGUIArgValue().getExpression() instanceof ASTCallExpression
            && ((ASTCallExpression) event.getGUIArgValue().getExpression()).getExpression() instanceof ASTNameExpression) {
          ASTCallExpression call = (ASTCallExpression) event.getGUIArgValue().getExpression();
          ASTNameExpression callName = (ASTNameExpression) call.getExpression();

          String methodName = callName.getName();

          // Check if event is special case
          Optional<EventType> eventTypeRes = Optional.empty();
          Optional<SymTypeExpression> teOpt = Optional.empty();
          if (!call.getArguments().getExpressionList().isEmpty()) {
            Type4Ast type4Ast = new Type4Ast();
            GUIDSLTraverser traverser = GUIDSLTypeTraverserFactory.getInstance()
                .createTraverserForOO(type4Ast);
            call.getArguments().getExpression(0).accept(traverser);
            SymTypeExpression te = type4Ast.getTypeOfExpression(call.getArguments().getExpression(0));
            teOpt = Optional.of(te);
            if(te.hasTypeInfo() && (util.isDomainType(te) || util.isDomainListType(te))) {
              for (EventType eventType : EventType.values()) {
                if (methodName.equals(eventType.eventName)) {
                  eventTypeRes = Optional.of(eventType);
                }
              }
            }
          }

          // Assoc related buildins need special parameter/arg handling
          EventArgAndParamResult res;
          List<EventType> assocEvents = List.of(EventType.ASSOC_ADD, EventType.ASSOC_REMOVE);
          if(eventTypeRes.isPresent() && assocEvents.contains(eventTypeRes.get())) {
            res = convertEventArgsAndParams4Assocs(call);
          }else{
            res = convertEventArgsAndParams(call);
          }

          // Assoc related methods need to have a unique name for TS gen
          String methodNamePostfix = res.referencedAssoc.map(r -> "_" + r.getRoleName()).orElse("");
          String fullMethodName = methodName + methodNamePostfix;

          getCD4C().addMethod(decoratedClass, getEventArgCallbackTemplate(),
              util.gui2cdEventCallName(guiComponent, event.getName()), fullMethodName, res.argCallbackArgs, res.argCallbackParams);

          Optional<ASTCDMethodSignature> methodSignature = getCD4C().createMethod(decoratedClass,
              getEventCallbackTemplate(), fullMethodName, res.callbackArgs, res.callbackParams);
          if (methodSignature.isPresent() && !GenService.containsMethod(decoratedClass, methodSignature.get())) {
            decoratedClass.addCDMember(methodSignature.get());

            if(eventTypeRes.isPresent()) {
              if(res.referencedAssoc.isEmpty()) {
                glex.replaceTemplate(EMPTY_BODY, methodSignature.get(), new TemplateHookPoint(getEventTypeTemplate(eventTypeRes.get()),
                    util.getMainDomainModelSimpleName().get(), teOpt.get().print(), res.argCallbackArgs.get(0)));
              }else{
                glex.replaceTemplate(EMPTY_BODY, methodSignature.get(), new TemplateHookPoint(getEventTypeTemplate(eventTypeRes.get()),
                    util.getMainDomainModelSimpleName().get(), teOpt.get().print(), res.argCallbackArgs.get(0), res.referencedAssoc.get()));
              }
            }
          }
          if (UMLPGeneratorConfig.getInstance().isUseJsweet()) {
            addImport(decoratedClass, "java.util.function.Consumer");
          }
        }
      }
    }
  }

  public static class EventArgAndParamResult{
    final List<String> callbackArgs = new ArrayList<>();
    final List<String> callbackParams = new ArrayList<>();
    final List<String> argCallbackArgs = new ArrayList<>();
    final List<String> argCallbackParams = new ArrayList<>();
    Optional<ReferencedAssoc> referencedAssoc = Optional.empty();
  }

  public static class ReferencedAssoc{
    private final ASTExpression linkSourceAccessor;
    private final String printedLinkSourceAccessor;
    private final String roleName;
    private final boolean mult;

    public ASTExpression getLinkSourceAccessor() {
      return linkSourceAccessor;
    }

    public String getPrintedLinkSourceAccessor() {
      return printedLinkSourceAccessor;
    }

    public String getRoleName() {
      return roleName;
    }

    public boolean isMult() {
      return mult;
    }

    protected ReferencedAssoc(ASTExpression linkSourceAccessor, String printedExpression, String roleName, boolean mult) {
      this.linkSourceAccessor = linkSourceAccessor;
      this.printedLinkSourceAccessor = printedExpression;
      this.roleName = roleName;
      this.mult = mult;
    }
  }

  protected ReferencedAssoc extractReferencedAssoc(ASTFieldAccessExpression e){
    Type4Ast type4Ast = new Type4Ast();
    GUIDSLTraverser traverser = GUIDSLTypeTraverserFactory.getInstance()
        .createTraverserForOO(type4Ast);
    e.accept(traverser);
    SymTypeExpression ste = type4Ast.getTypeOfExpression(e);

    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(guiAst));

    // replace field access with getters
    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, iterateItems, iterateItems
    );

    return new ReferencedAssoc(e.getExpression(), getterPrinter.prettyprint(e.getExpression()), e.getName(), GenService.isList(ste));
  }

  protected EventArgAndParamResult convertEventArgsAndParams(ASTCallExpression call) {
    EventArgAndParamResult res = new EventArgAndParamResult();

    for (ASTExpression e : call.getArguments().getExpressionList()) {
      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(guiAst));

      // replace field access with getters
      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, iterateItems
      );

      String getterValue = getterPrinter.prettyprint(e);

      if (!iterateItems.contains(e)) {
        res.argCallbackArgs.add(getterValue);
      } else {
        res.argCallbackArgs.add(util.print(e));
        res.argCallbackParams.add(util.print(e));
      }
      res.callbackArgs.add(util.print(e));
      res.callbackParams.add(util.print(e).replaceAll("\\W+", "_"));
    }
    return res;
  }

  protected EventArgAndParamResult convertEventArgsAndParams4Assocs(ASTCallExpression call) {
    EventArgAndParamResult res = new EventArgAndParamResult();
    List<ASTExpression> expressions = call.getArguments().getExpressionList();

    // First argument must reference an association!
    ASTExpression assocRef = expressions.get(0);
    if(!(assocRef instanceof ASTFieldAccessExpression)){
      Log.error("Can not extract referenced association from expression '" + GUIDSLMill.prettyPrint(call, false) + "'", call.get_SourcePositionStart(), call.get_SourcePositionEnd());
      return null;
    }
    res.referencedAssoc = Optional.of(extractReferencedAssoc((ASTFieldAccessExpression) assocRef));

    // Rest as normal
    for (ASTExpression e : expressions.subList(1, expressions.size())) {
      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(guiAst));

      // replace field access with getters
      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, iterateItems
      );

      String getterValue = getterPrinter.prettyprint(e);

      if (!iterateItems.contains(e)) {
        res.argCallbackArgs.add(getterValue);
      } else {
        res.argCallbackArgs.add(util.print(e));
        res.argCallbackParams.add(util.print(e));
      }
      res.callbackArgs.add(util.print(e));
      res.callbackParams.add(util.print(e).replaceAll("\\W+", "_"));
    }
    return res;
  }

  protected String getEventTemplate() {
    return "umlp.gui2java.Event";
  }

  protected String getEventCallbackTemplate() {
    return "umlp.gui2java.EventCallback";
  }

  protected String getEventArgCallbackTemplate() {
    return "umlp.gui2java.EventArgCallback";
  }

  protected String getEventTypeTemplate(EventType inbuild) {
    switch (inbuild) {
      case CREATE:
        return "umlp.gui2java.CreateEvent";
      case DELETE:
        return "umlp.gui2java.DeleteEvent";
      case ASSOC_ADD:
        return "umlp.gui2java.AssocAddEvent";
      case ASSOC_REMOVE:
        return "umlp.gui2java.AssocRemoveEvent";
    }
    return "";
  }

  public enum EventType {
    DELETE("delete"),
    CREATE("create"),
    ASSOC_ADD("assoc_add"),
    ASSOC_REMOVE("assoc_remove");

    final String eventName;

    EventType(String eventName){
      this.eventName = eventName;
    }
  }
}

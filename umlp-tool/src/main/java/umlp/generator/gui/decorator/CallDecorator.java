/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import umlp.generator.gui.GUIComponentAccessVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.*;
import java.util.stream.Collectors;

import static umlp.generator.gui.decorator.DomainRefDecorator.BUILDER;

public class CallDecorator extends GUIComponentDecorator {

  public CallDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    // Get all ASTGUIComponents
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);
    List<ASTCallExpression> allCalls = viewComponents.getCallExpressions();
    boolean isCreate = isCreateClass(decoratedClass);

    allCalls.forEach(call -> {
          if (!isNavigateComponent(call)) {
            StringBuilder sb = new StringBuilder();
            for (ASTExpression argument : call.getArguments().getExpressionList()) {
              if (!(argument instanceof ASTNameExpression)) {
                continue;
              }
              String paramName = ((ASTNameExpression) argument).getName();
              Optional<GUIParamSymbol> paramSymbol = ((GUIDSLScope) argument.getEnclosingScope()).resolveGUIParam(paramName);
              if (paramSymbol.isEmpty()) {
                continue;
              }
              TypeSymbol typeInfo = paramSymbol.get().getType().getTypeInfo();
              if (sb.length() != 0) {
                sb.append(", ");
              }
              sb.append(typeInfo.getName()).append(" ").append(paramName); //TODO MB
            }
            getCD4C().addMethod(decoratedClass, "umlp.gui2java.Call",
                getMethodName(call), sb.toString()
                /* new GUIDSLFullPrettyPrinter().prettyprint(call))*/);
          }
          else {
            ASTGUIComponent arg = (ASTGUIComponent) call.getArguments().getExpressionList().get(0);
            List<String> args = arg.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> a.getExpression() instanceof ASTNameExpression)
                .map(a -> ((ASTNameExpression) a.getExpression()).getName()).collect(Collectors.toList()); //TODO GV

            Map<String, String> parameters = new HashMap<>();
            List<String> arguments = new ArrayList<>();
            args.forEach(a -> {
              Optional<GUIParamSymbol> argSymbol = guiComponent.getEnclosingScope().resolveGUIParam(a);
              argSymbol.ifPresent((s) -> {
                if (GUIGeneratorUtil.isDomainType(s.getType())) {
                  parameters.put((isCreate ? a + BUILDER : a), s.getType().getTypeInfo().getName());
                  arguments.add(isCreate ? a + BUILDER : a);
                }
                else {
                  arguments.add(a);
                }
              });
            });
            String result = String.join(",", arguments);

            getCD4C().addMethod(decoratedClass, (isCreate ? "umlp.gui2java.NavigateBuilder" : "umlp.gui2java.Navigate"),
                "navigate", arg.getName(), result, parameters);
          }
        }
    );

  }

}

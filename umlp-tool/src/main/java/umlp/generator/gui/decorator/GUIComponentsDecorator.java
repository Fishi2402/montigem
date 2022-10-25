package umlp.generator.gui.decorator;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.Joiners;
import umlp.generator.gui.GUIComponentAccessVisitor;
import umlp.generator.gui.GUIComponentsAccessVisitor;

import java.util.ArrayList;
import java.util.List;

public class GUIComponentsDecorator extends GUIAbstractDecorator {

  private final List<ASTGUIComponent> inputComponents;
  private final GUIComponentsAccessVisitor viewComponent;

  public GUIComponentsDecorator(final GlobalExtensionManagement glex, GUIComponentsAccessVisitor viewComponent, List<ASTGUIComponent> inputComponents) {
    super(glex);
    this.viewComponent = viewComponent;
    this.inputComponents = inputComponents;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDClass decoratedClass) {
    TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());

   /* viewComponent.getNameExpressions().forEach(expr -> {
      String componentName = getComponentName(expr);
      componentName = "Button";
      String expressionName = expr.getName();
      Optional<TypeSymbol> type = getScope(expr.getEnclosingScope()).resolveType(componentName);
      if (type.isPresent()) {
        List<FunctionSymbol> functions = type.get().getFunctionList(expressionName);
        Optional<TypeSymbol> eventType = getScope(expr.getEnclosingScope()).resolveType(EVENT.getName());
        Optional<TypeSymbol> boxedVoidType = getScope(expr.getEnclosingScope()).resolveType(VOID_BOXED.getName());
        if (functions.size() == 1 && functions.get(0).getType().isVoidType()
            && (functions.get(0).getParameterList().size() == 1 || functions.get(0).getParameterList().size() == 0)
            && eventType.isPresent()
            && boxedVoidType.isPresent()
        ) {
          SymTypeExpression typeVarExpr;
          if (functions.get(0).getParameterList().size() == 0) {
            typeVarExpr = SymTypeExpressionFactory.createTypeVariable(boxedVoidType.get());
          } else {
            typeVarExpr = functions.get(0).getParameterList().get(0).getType();
          }
          Optional<SymTypeOfGenerics> standardCalculation = Optional.of(SymTypeExpressionFactory.createGenerics(eventType.get(), typeVarExpr));
        }
      }
    });*/

    filterComponentsWithAllPredicates(viewComponent.getComponents(), Lists.newArrayList(PREDICATE_NOT_TABLE, PREDICATE_NOT_BUTTON))
        .forEach(c -> new InputValueDecorator(glex, guiAst).transform(decoratedClass, c, viewComponent.getComponentName(c)));

    filterComponentsWithPredicate(viewComponent.getComponents(), PREDICATE_NOT_TABLE).forEach(c -> new CallDecorator(glex, guiAst).transform(decoratedClass, c, viewComponent.getComponentName(c)));

    viewComponent.getComponents().stream().filter(c -> !filterArgumentsWithPredicate(new GUIComponentAccessVisitor(c).getNamedArguments(), PREDICATE_LEFTCLICK).isEmpty())
        .forEach(c -> new LeftClickDecorator(glex, guiAst)
            .transform(decoratedClass, c, viewComponent.getComponentName(c)));

    //------------------ TABLES ----------------------------------------------

    List<HookPoint> res = new ArrayList<>(); // List of HookPoints for table components to use in the init() method
    List<TemplateHookPoint> columnTemplates = new ArrayList<>();
    List<TemplateHookPoint> rowsTemplates = new ArrayList<>();
    List<TableDecorator> tableDecorators = new ArrayList<>();

    filterComponentsWithPredicate(viewComponent.getComponents(), PREDICATE_TABLE).forEach(c -> tableDecorators.add(new TableDecorator(glex, guiAst, c, viewComponent.getComponentName(c))));
    tableDecorators.forEach(td -> {
      td.transform(decoratedClass);
      columnTemplates.add(new TemplateHookPoint("umlp.gui2java.InitTableColumns", td.getComponentName(), td.getInitTableColumnsArguments(), guiDSLPrinter));
      rowsTemplates.add(new TemplateHookPoint("umlp.gui2java.InitRows", td.getComponentName(), td.getInitRowsArgument()));
    });

    columnTemplates.forEach(res::add);
    rowsTemplates.forEach(res::add);
    glex.replaceTemplate("umlp.gui2java.InitTables", res);

    //------------------ PIE ----------------------------------------------

    res = new ArrayList<>(); // List of HookPoints for table components to use in the init() method
    List<TemplateHookPoint> pieEntriesTemplates = new ArrayList<>();
    List<PieDecorator> pieDecorators = new ArrayList<>();

    filterComponentsWithPredicate(viewComponent.getComponents(), PREDICATE_PIE).forEach(c -> pieDecorators.add(new PieDecorator(glex, guiAst, c, viewComponent.getComponentName(c))));
    pieDecorators.forEach(td -> {
      td.transform(decoratedClass);
      pieEntriesTemplates.add(new TemplateHookPoint("umlp.gui2java.InitPieEntries", td.getComponentName(), Joiners.COMMA.join(td.getInitPieEntriesArguments())));
    });

    pieEntriesTemplates.forEach(res::add);
    glex.replaceTemplate("umlp.gui2java.InitPieCharts", res);
  }

  /*public IBasicSymbolsScope getScope(IExpressionsBasisScope expressionsBasisScope) {
    // is accepted only here, decided on 07.04.2020
    if (!(expressionsBasisScope instanceof IBasicSymbolsScope)) {
      Log.error("0xA2308 the enclosing scope of the expression does not implement the interface IBasicSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IBasicSymbolsScope) expressionsBasisScope;
  }

  protected String getComponentName(ASTExpression expr) {
    IExpressionsBasisScope scope = expr.getEnclosingScope();
    while (scope.getEnclosingScope() != null) {
      if (scope.getEnclosingScope() instanceof IExpressionsBasisArtifactScope) {
        return ((IExpressionsBasisArtifactScope) scope.getEnclosingScope()).getFullName();
      }
      scope = scope.getEnclosingScope();
    }
    return "";
  }*/

}

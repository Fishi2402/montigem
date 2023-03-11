/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUINamedArgAccessVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TableDecorator extends GUI2JavaComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_ROWS = c -> c.getName().equals("rows");
  public static final Predicate<ASTGUINamedArg> PREDICATE_COLS = c -> c.getName().equals("columns");

  protected final ASTGUIComponent guiComponent;
  protected final String componentName;

  protected List<String> tableColumnsArgs = new ArrayList<>();
  protected String rowsArg;

  public TableDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst, ASTGUIComponent guiComponent, String componentName) {
    super(glex, guiAst);
    this.guiComponent = guiComponent;
    this.componentName = componentName;
  }

  public void transform(ASTCDClass decoratedClass) {
    Optional<ASTGUINamedArg> col = getComponentArgOptional(guiComponent, PREDICATE_COLS);
    Optional<ASTGUINamedArg> row = getComponentArgOptional(guiComponent, PREDICATE_ROWS);

    MethodDecorator decorator = new MethodDecorator(glex);

    if (row.isPresent()) {
      ASTCDAttribute rowsAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
          "List<? extends Object>", StringTransformations.uncapitalize(guiComponent.getName()) +
              StringTransformations.capitalize(componentName) + "Rows");
      glex.replaceTemplate("cd2java.Value", rowsAttribute, new StringHookPoint(" = new ArrayList<>()"));
      getCD4C().addImport(decoratedClass, "java.util.List");
      getCD4C().addImport(decoratedClass, "java.util.ArrayList");
      decoratedClass.addCDMember(rowsAttribute);

      decoratedClass.addAllCDMembers(decorator.decorate(rowsAttribute));

      if (row.get().getGUIArgValue().isPresentExpression() && row.get().getGUIArgValue().getExpression() instanceof ASTFieldAccessExpression) {
        rowsArg = handleZuweisung((ASTFieldAccessExpression) row.get().getGUIArgValue().getExpression(), decoratedClass,
            guiComponent, componentName, false).orElse(guiDSLPrinter.prettyprint(row.get().getGUIArgValue()));
      }
      else {
        rowsArg = guiDSLPrinter.prettyprint(row.get().getGUIArgValue());
      }
    }

    if (col.isPresent()) {
      getCD4C().addImport(decoratedClass, "stanley.tmp.table.tabletypes.TableColumn"); // TODO TODO GV, Age

      ASTCDAttribute columnsAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
          "List<TableColumn>", StringTransformations.uncapitalize(guiComponent.getName()) +
              StringTransformations.capitalize(componentName) + "Columns");
      glex.replaceTemplate("cd2java.Value", columnsAttribute, new StringHookPoint(" = new ArrayList<>()"));
      getCD4C().addImport(decoratedClass, "java.util.List");
      getCD4C().addImport(decoratedClass, "java.util.ArrayList");
      decoratedClass.addCDMember(columnsAttribute);

      decoratedClass.addAllCDMembers(decorator.decorate(columnsAttribute));

      GUINamedArgAccessVisitor viewComponents = new GUINamedArgAccessVisitor(col.get());
      viewComponents.getCallExpressions().stream().filter(c -> (c.getExpression() instanceof ASTNameExpression) &&
          (((ASTNameExpression) c.getExpression()).getName().equals("TableColumn") ||
              ((ASTNameExpression) c.getExpression()).getName().equals("List"))).forEach(e -> {
        List<String> args = new ArrayList<>();
        e.getArguments().getExpressionList().stream().filter(ex -> ex instanceof ASTLiteralExpression).forEach(a ->
            args.add(guiDSLPrinter.prettyprint(a)));
        e.getArguments().getExpressionList().stream().filter(ex -> ex instanceof ASTFieldAccessExpression).forEach(a -> {
          handleZuweisung((ASTFieldAccessExpression) a, decoratedClass, guiComponent, componentName, true)
              .ifPresent(args::add);
        });
        tableColumnsArgs.add(Joiners.COMMA.join(args));
      });

    }

    //  getCD4C().addAttribute(decoratedClass, true, true, "protected List<? extends Object> rows = new ArrayList<>()"); // Funktioniert nicht wegen der Zuweisung
    //  getCD4C().addAttribute(decoratedClass, true, true, "protected List<Col> tableColumns = new ArrayList<>()");
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    transform(decoratedClass);
  }

  @Override
  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String variableType, String attrName) {
  }

  public List<String> getInitTableColumnsArguments() {
    return tableColumnsArgs;
  }

  public String getInitRowsArgument() {
    return rowsArg;
  }

  public String getComponentName() {
    return componentName;
  }
}


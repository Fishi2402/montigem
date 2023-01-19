/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.decorator;

import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_ROWS = c -> c.getName().equals("rows");
  public static final Predicate<ASTGUINamedArg> PREDICATE_COLS = c -> c.getName().equals("cols");

  protected final ASTGUIComponent guiComponent;
  protected final String componentName;

  protected List<ASTExpression> tableColumnsArgs = new ArrayList<>();
  protected String rowsArg = "";

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
          "List<? extends Object>", "rows" + componentName);
      glex.replaceTemplate("cd2java.Value", rowsAttribute, new StringHookPoint(" = new ArrayList<>()"));
      getCD4C().addImport(decoratedClass, "java.util.List");
      getCD4C().addImport(decoratedClass, "java.util.ArrayList");

      decoratedClass.addCDMember(rowsAttribute);
      decoratedClass.addAllCDMembers(decorator.decorate(rowsAttribute));
      rowsArg = guiDSLPrinter.prettyprint(row.get().getGUIArgValue().getExpression());
    }

    if (col.isPresent()) {
      ASTCDAttribute columnsAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
          "List<TableColumn>", "tableColumns" + componentName);
      glex.replaceTemplate("cd2java.Value", columnsAttribute, new StringHookPoint(" = new ArrayList<>()"));
      getCD4C().addImport(decoratedClass, "java.util.List");
      getCD4C().addImport(decoratedClass, "java.util.ArrayList");

      decoratedClass.addCDMember(columnsAttribute);
      decoratedClass.addAllCDMembers(decorator.decorate(columnsAttribute));

      if (col.get().getGUIArgValue().getExpression() instanceof ASTCallExpression) {
        List<ASTExpression> var = ((ASTCallExpression) col.get().getGUIArgValue().getExpression())
            .getArguments().getExpressionList().stream().filter(e -> e instanceof ASTCallExpression)
            .flatMap(e -> ((ASTCallExpression) e).getArguments().getExpressionList().stream()).collect(Collectors.toList());
        tableColumnsArgs.addAll(var);
      }

    }

    //  getCD4C().addAttribute(decoratedClass, true, true, "protected List<? extends Object> rows = new ArrayList<>()"); // Funktioniert nicht wegen der Zuweisung
    //  getCD4C().addAttribute(decoratedClass, true, true, "protected List<Col> tableColumns = new ArrayList<>()");

  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    transform(decoratedClass);
  }

  public List<ASTExpression> getInitTableColumnsArguments() {
    return tableColumnsArgs;
  }

  public String getInitRowsArgument() {
    return rowsArg;
  }

  public String getComponentName() {
    return componentName;
  }
}


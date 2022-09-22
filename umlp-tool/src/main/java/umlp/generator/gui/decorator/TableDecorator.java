package umlp.generator.gui.decorator;

import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_ROWS = c -> c.getName().equals("rows");
  public static final Predicate<ASTGUINamedArg> PREDICATE_COLS = c -> c.getName().equals("cols");

  public TableDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    List<ASTGUINamedArg> rows = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), PREDICATE_ROWS);

    Optional<ASTGUINamedArg> col = getColumnArg(guiComponent);
    if (rows.size() != 1) {
      Log.error("TableDecorator GUIComponent args aren't correct");
      return;
    }
    ASTGUINamedArg row = rows.get(0);

    MethodDecorator decorator = new MethodDecorator(glex);

    ASTCDAttribute rowsAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
        "List<? extends Object>", "rows");
    glex.replaceTemplate("cd2java.Value", rowsAttribute, new StringHookPoint(" = new ArrayList<>()"));
    decoratedClass.addCDMember(rowsAttribute);
    decoratedClass.addAllCDMembers(decorator.decorate(rowsAttribute));

    ASTCDAttribute columnsAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
        "List<Col>", "tableColumns");
    glex.replaceTemplate("cd2java.Value", columnsAttribute, new StringHookPoint(" = new ArrayList<>()"));
    decoratedClass.addCDMember(columnsAttribute);
    decoratedClass.addAllCDMembers(decorator.decorate(columnsAttribute));

  //  getCD4C().addAttribute(decoratedClass, true, true, "protected List<? extends Object> rows = new ArrayList<>()"); // Funktioniert nicht wegen der Zuweisung
  //  getCD4C().addAttribute(decoratedClass, true, true, "protected List<Col> tableColumns = new ArrayList<>()");

  }

  protected Optional<ASTGUINamedArg> getColumnArg(ASTGUIComponent guiComponent) {
    List<ASTGUINamedArg> cols = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), PREDICATE_COLS);
    if (cols.size() == 1) {
      return Optional.of(cols.get(0));
    }
    return Optional.empty();
  }

}


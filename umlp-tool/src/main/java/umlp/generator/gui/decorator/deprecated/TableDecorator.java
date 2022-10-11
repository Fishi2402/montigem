package umlp.generator.gui.decorator.deprecated;

import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import umlp.generator.gui.decorator.GUIComponentDecorator;

import java.util.Optional;
import java.util.function.Predicate;

public class TableDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_ROWS = c -> c.getName().equals("rows");
  public static final Predicate<ASTGUINamedArg> PREDICATE_COLS = c -> c.getName().equals("cols");

  public TableDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    Optional<ASTGUINamedArg> col = getComponentArgOptional(guiComponent, PREDICATE_COLS);
    Optional<ASTGUINamedArg> row = getComponentArgSingle(guiComponent, PREDICATE_ROWS);

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

}


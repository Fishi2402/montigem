package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GUI2JavaGuardDecorator extends GUIAbstractDecorator {

  protected final ASTGUICompilationUnit guiAst;

  public GUI2JavaGuardDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex);
    this.guiAst = guiAst;
  }

  public void transform(ASTCDClass decoratedClass, ASTGUIGuard guard) {
    IterationItemsFinder finder = new IterationItemsFinder();
    Collection<ASTExpression> ignoreOnPrint = new HashSet<>(finder.getItems(guiAst));

    // replace field access with getters
    Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
        new IndentPrinter(), false, ignoreOnPrint, ignoreOnPrint
    );

    Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

    String nullCheck = nullCheckPrinter.prettyprint(guard.getCondition());
    String getterValue = getterPrinter.prettyprint(guard.getCondition());

    String getterName = util.gui2cdAttrGetterName(guard.getId().getName(), "condition");

    List<VariableSymbol> iterItemSymbols = util.findIterationItemSymbols(guard.getEnclosingScope());
    List<String> iterItemArgs = getArgs(iterItemSymbols);

    addGetComponentMethod(decoratedClass, getterName, "boolean",
        getterValue, iterItemArgs, nullCheck);
  }

  protected void addGetComponentMethod(
      ASTCDClass decoratedClass, String methodName, String attrType, String returnValue,
      List<String> arguments, String condition
  ) {
    getCD4C().addMethod(
        decoratedClass, getGetterTemplate(), methodName, attrType,
        returnValue, String.join(", ", arguments), condition
    );
  }

  protected String getGetterTemplate() {
    return "umlp.gui2java.GetGuardReplaced";
  }

  public List<String> getArgs(List<VariableSymbol> symbols) {
    List<String> res = new ArrayList<>();
    for (VariableSymbol sym : symbols) {
      res.add(sym.getType().getTypeInfo().getName() + " " + sym.getName());
    }
    return res;
  }

}

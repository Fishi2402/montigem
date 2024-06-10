package umlp.generator.gui.genjava.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * Find iteration items. The items in the context of GUI generator must be ignored when printing
 * expressions as getters, setters, etc. For example, iteration items declared in iterate statement
 * are local to HTML context, meaning that those are referenced as raw variables and do not have
 * getters.
 * <pre>
 * {@code
 * ```gui
 * page Foo(List<String> strs, String bar) {
 *   @iterate(String s : strs) {
 *     baz@GemText(value = bar + s);
 *   }
 * }
 * ```
 * ```html
 * <ng-container *ngFor="let s of strs">
 *   <gem-text [value]='getBazValue(s)'>
 *   </gem-text>
 * </ng-container>
 * ```
 * ```java
 * public getBazValue(String s) {
 *   return getBar() + s;
 * }
 * ```
 * }
 * </pre>
 */
public class IterationItemsFinder implements ExpressionsBasisVisitor2 {

  protected ExpressionsBasisTraverser traverser;

  protected Collection<ASTNameExpression> items;
  protected final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  public IterationItemsFinder() {
    traverser = GUIDSLMill.traverser();
    traverser.add4ExpressionsBasis(this);
  }

  /**
   * Find all expressions in the given tree that reference iteration items defined in the enclosing
   * scopes for the expression including ancestor scopes.
   *
   * @param tree AST to search expressions in.
   * @return A collection of expressions that must be ignored when printing expressions to use
   * accessors and mutators instead of direct attribute access.
   */
  public Collection<ASTNameExpression> getItems(ASTNode tree) {
    items = new HashSet<>();
    tree.accept(traverser);
    return new HashSet<>(items);
  }

  @Override
  public void visit(ASTNameExpression node) {
    Optional<VariableSymbol> vs = util.findIterationItemSymbols(
        node.getName(), node.getEnclosingScope()
    );
    if (vs.isPresent()) {
      items.add(node);
    }
  }

}

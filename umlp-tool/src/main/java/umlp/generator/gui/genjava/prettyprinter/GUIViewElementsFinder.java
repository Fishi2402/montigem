package umlp.generator.gui.genjava.prettyprinter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GUIViewElementsFinder implements ExpressionsBasisVisitor2 {
  protected static Cache<Map.Entry<String, IExpressionsBasisScope>, Optional<VariableSymbol>> cache
      = CacheBuilder.newBuilder().softValues().build();

  protected ExpressionsBasisTraverser traverser;

  protected Collection<ASTNameExpression> items;
  protected final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  public GUIViewElementsFinder() {
    traverser = GUIDSLMill.traverser();
    traverser.add4ExpressionsBasis(this);
  }

  /**
   * Find all expressions in the given tree that reference GUIViewElements defined in the enclosing
   * scopes for the expression including ancestor scopes.
   *
   * @param tree AST to search expressions in.
   * @return A collection of expressions that must be ignored when printing expressions to use
   * accessors and mutators instead of direct attribute access.
   */
  public Collection<ASTNameExpression> getGUIViewElements(ASTNode tree) {
    items = new HashSet<>();
    tree.accept(traverser);
    return new HashSet<>(items);
  }

  @Override
  public void visit(ASTNameExpression node) {
    Optional<VariableSymbol> vs;
    try {
      var key = Map.entry(node.getName(), node.getEnclosingScope());
      vs = cache.get(
          key,
          () -> {
            Optional<IGUIDSLScope> scope = util.findClosestGUIDSLScope(node.getEnclosingScope());
            if (scope.isPresent()) {
              Optional<VariableSymbol> sym = scope.get().resolveVariable(node.getName());
              if (sym.isPresent() && (util.isViewElement(sym.get().getType())
                  || util.isViewElementsCollection(sym.get().getType())
              )) {
                return sym;
              }
            }
            return Optional.empty();
          }
      );
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }

    if (vs.isPresent()) {
      items.add(node);
    }
  }

}

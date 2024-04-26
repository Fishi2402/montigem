package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIBlock;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIList;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class DependencyScopeTree implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected GUIDSLTraverser traverser;

  protected static DependencyScopeTree instance;

  protected Stack<DependencyScope> scopeStack = new Stack<>();
  protected Optional<DependencyScope> reverseTreeRoot = Optional.empty();
  protected Map<ASTNode, DependencyScope> scopeMap = new HashMap<>();
  protected Optional<ASTNode> jump;

  public static DependencyScopeTree getInstance() {
    if (instance == null) {
      instance = new DependencyScopeTree();
    }
    return instance;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  private DependencyScopeTree() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
  }

  public void init(ASTGUIComponentDeclaration node) {
    reset();
    node.accept(getTraverser());
  }

  public void reset() {
  }

  public Optional<DependencyScope> getScope(ASTNode node) {
    return Optional.ofNullable(scopeMap.get(node));
  }

  public Optional<DependencyScope> getEnclosingScope(ASTNode node) {
    if (scopeMap.containsKey(node)) {
      return Optional.ofNullable(scopeMap.get(node).getEnclosing());
    }
    return Optional.empty();
  }

  @Override
  public void visit(ASTGUIComponentDeclaration node) {
    DependencyScope scope = putScope(node);
    setReverseTreeRoot(scope);
  }

  @Override
  public void visit(ASTGUIBlock node) {
    DependencyScope scope = putScope(node);
    setReverseTreeRoot(scope);
  }

  @Override
  public void endVisit(ASTGUIBlock node) {
    popScope();
  }

  @Override
  public void visit(ASTGUIParam node) {
    putScope(node);
  }

  @Override
  public void endVisit(ASTGUIParam node) {
    popScope();
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (!(node.getIterateControl() instanceof ASTGUIShortIterateControl)) {
      Log.error("0x00004 iterate construct " + node.getId().getName()
          + " was not processed: only short version, such as '(Type item : list)',"
          + " is supported so far");
    }
    ASTGUIShortIterateControl control = (ASTGUIShortIterateControl) node.getIterateControl();
    DependencyScope scope = putScope(control);
    setReverseTreeRoot(scope);
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (!(node.getIterateControl() instanceof ASTGUIShortIterateControl)) {
      Log.error("0x00004 iterate construct " + node.getId().getName()
          + " was not processed: only short version, such as '(Type item : list)',"
          + " is supported so far");
    }
    popScope();
  }

  @Override
  public void visit(ASTCallExpression node) {
    rememberScopeStackJump(node);
  }

  @Override
  public void endVisit(ASTCallExpression node) {
    putScope(node);
    maybeJumpScopeStack(node);
  }

  @Override
  public void visit(ASTGUIList node) {
    rememberScopeStackJump(node);
  }

  @Override
  public void endVisit(ASTGUIList node) {
    putScope(node);
    maybeJumpScopeStack(node);
  }

  protected DependencyScope putScope(ASTNode node) {
    DependencyScope current = new DependencyScope(node);
    if (!scopeStack.isEmpty()) {
      DependencyScope enclosing = scopeStack.peek();
      current.setEnclosing(enclosing);
    }
    scopeStack.push(current);
    return current;
  }

  public void setReverseTreeRoot(DependencyScope scope) {
    this.reverseTreeRoot = Optional.of(scope);
  }

  public void rememberScopeStackJump(ASTNode node) {
    if (reverseTreeRoot.isPresent() && reverseTreeRoot.get().equals(scopeStack.peek())) {
      jump = Optional.of(node);
    }
  }

  public void maybeJumpScopeStack(ASTNode node) {
    if (jump.isPresent() && jump.get().equals(node)) {
      while (!scopeStack.isEmpty()
          || (reverseTreeRoot.isPresent() && !reverseTreeRoot.get().equals(scopeStack.peek()))) {
        scopeStack.pop();
      }
    }
  }

  protected void popScope() {
    if (scopeStack.isEmpty()) {
      Log.error("0x0001C Unable to derive initialization dependencies.");
    }
    scopeStack.pop();
  }

}
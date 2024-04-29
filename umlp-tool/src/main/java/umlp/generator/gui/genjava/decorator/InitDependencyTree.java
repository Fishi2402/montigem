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

public class InitDependencyTree implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected GUIDSLTraverser traverser;

  protected static InitDependencyTree instance;

  protected Stack<InitDependency> depStack = new Stack<>();
  protected Optional<InitDependency> reverseTreeRoot = Optional.empty();
  protected Map<ASTNode, InitDependency> nodeDepMap = new HashMap<>();
  protected Optional<ASTNode> jump;

  public static InitDependencyTree getInstance() {
    if (instance == null) {
      instance = new InitDependencyTree();
    }
    return instance;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  private InitDependencyTree() {
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

  public Optional<InitDependency> getDependency(ASTNode node) {
    return Optional.ofNullable(nodeDepMap.get(node));
  }

  @Override
  public void visit(ASTGUIComponentDeclaration node) {
    InitDependency scope = putDependency(node);
    setReverseTreeRoot(scope);
  }

  @Override
  public void visit(ASTGUIBlock node) {
    InitDependency scope = putDependency(node);
    setReverseTreeRoot(scope);
  }

  @Override
  public void endVisit(ASTGUIBlock node) {
    popDependency();
  }

  @Override
  public void visit(ASTGUIParam node) {
    putDependency(node);
  }

  @Override
  public void endVisit(ASTGUIParam node) {
    popDependency();
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (!(node.getIterateControl() instanceof ASTGUIShortIterateControl)) {
      Log.error("0x00004 iterate construct " + node.getId().getName()
          + " was not processed: only short version, such as '(Type item : list)',"
          + " is supported so far");
    }
    ASTGUIShortIterateControl control = (ASTGUIShortIterateControl) node.getIterateControl();
    InitDependency scope = putDependency(control);
    setReverseTreeRoot(scope);
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (!(node.getIterateControl() instanceof ASTGUIShortIterateControl)) {
      Log.error("0x00004 iterate construct " + node.getId().getName()
          + " was not processed: only short version, such as '(Type item : list)',"
          + " is supported so far");
    }
    popDependency();
  }

  @Override
  public void visit(ASTCallExpression node) {
    rememberScopeStackJump(node);
  }

  @Override
  public void endVisit(ASTCallExpression node) {
    putDependency(node);
    maybeJumpScopeStack(node);
  }

  @Override
  public void visit(ASTGUIList node) {
    rememberScopeStackJump(node);
  }

  @Override
  public void endVisit(ASTGUIList node) {
    putDependency(node);
    maybeJumpScopeStack(node);
  }

  protected InitDependency putDependency(ASTNode node) {
    InitDependency current = new InitDependency();
    if (!depStack.isEmpty()) {
      InitDependency enclosing = depStack.peek();
      current.setDependent(enclosing);
    }
    depStack.push(current);
    return current;
  }

  public void setReverseTreeRoot(InitDependency scope) {
    this.reverseTreeRoot = Optional.of(scope);
  }

  public void rememberScopeStackJump(ASTNode node) {
    if (reverseTreeRoot.isPresent() && reverseTreeRoot.get().equals(depStack.peek())) {
      jump = Optional.of(node);
    }
  }

  public void maybeJumpScopeStack(ASTNode node) {
    if (jump.isPresent() && jump.get().equals(node)) {
      while (!depStack.isEmpty()
          || (reverseTreeRoot.isPresent() && !reverseTreeRoot.get().equals(depStack.peek()))) {
        depStack.pop();
      }
    }
  }

  protected void popDependency() {
    if (depStack.isEmpty()) {
      Log.error("0x0001C Unable to derive initialization dependencies.");
    }
    depStack.pop();
  }

}
package umlp.generator.gui.genjava.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;

import java.util.ArrayList;
import java.util.List;

public class DependencyScope {
  protected ASTNode node;
  protected DependencyScope enclosing;
  List<ASTCDMethodSignature> initMethods = new ArrayList<>();

  DependencyScope(ASTNode node) {
    this.node = node;
  }

  public ASTNode getNode() {
    return node;
  }

  public void setEnclosing(DependencyScope enclosing) {
    this.enclosing = enclosing;
  }

  public DependencyScope getEnclosing() {
    return enclosing;
  }

  public void addInitMethod(ASTCDMethodSignature signature) {
    initMethods.add(signature);
  }

  public List<ASTCDMethodSignature> getInitMethods() {
    return initMethods;
  }
}

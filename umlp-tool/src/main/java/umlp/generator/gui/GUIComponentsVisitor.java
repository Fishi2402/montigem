package umlp.generator.gui;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIFlex;
import de.monticore.guidsl._ast.ASTGUIVariable;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;

import java.util.ArrayList;
import java.util.List;

public class GUIComponentsVisitor implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  public List<ASTGUIComponent> getComponents() {
    return components;
  }

  protected List<ASTGUIComponent> components = new ArrayList<>();

  public GUIComponentsVisitor(ASTGUICompilationUnit node) {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    node.accept(getTraverser());
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void visit(ASTGUIComponent node) {
    components.add(node);
  }

}

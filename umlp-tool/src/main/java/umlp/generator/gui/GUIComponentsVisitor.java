/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;

import java.util.HashMap;
import java.util.Map;

public class GUIComponentsVisitor implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  protected Map<ASTGUIComponent, String> namedComponents = new HashMap();

  public static final String COMPONENT_NAME = "Comp";

  protected int counter = 1;

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
    namedComponents.put(node, getComponentName(node));
  }

  protected String getComponentName(ASTGUIComponent component) {
    return COMPONENT_NAME + counter++;
  }

  public Map<ASTGUIComponent, String> getNamedComponents() {
    return namedComponents;
  }


}

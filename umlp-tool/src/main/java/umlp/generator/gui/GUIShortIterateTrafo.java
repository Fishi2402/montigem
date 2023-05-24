/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;

public class GUIShortIterateTrafo implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;

  public GUIShortIterateTrafo() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTGUICompilationUnit ast) {
    ast.accept(getTraverser());
  }

  @Override
  public void visit(ASTGUIIterate node) {
    GUIShortIterateReplaceVisitor visitor = new GUIShortIterateReplaceVisitor();
    visitor.transform(node);
  }

}

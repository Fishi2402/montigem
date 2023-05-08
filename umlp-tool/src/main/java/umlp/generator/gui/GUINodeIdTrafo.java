/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.se_rwth.commons.StringTransformations;

// TODO AGe: potential name conflicts
public class GUINodeIdTrafo implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;
  protected int counter = 0;

  public GUINodeIdTrafo() {
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
  public void visit(ASTGUIComponent node) {
    if (!node.isPresentId()) {
      node.setId(
          GUIDSLMill.gUIVariableDeclarationBuilder()
              .setName(StringTransformations.uncapitalize(node.getName()) + counter++)
              .build()
      );
    }
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (!node.isPresentId()) {
      node.setId(
          GUIDSLMill.gUIVariableDeclarationBuilder()
              .setName("it" + counter++)
              .build()
      );
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    if (!node.isPresentId()) {
      node.setId(
          GUIDSLMill.gUIVariableDeclarationBuilder()
              .setName("guard" + counter++)
              .build()
      );
    }
  }

}

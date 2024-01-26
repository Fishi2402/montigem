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

import java.util.HashSet;
import java.util.Set;

// TODO AGe: potential name conflicts
public class GUINodeIdTrafo implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;
  protected int counter = 0;
  protected boolean collectNamesMode = true;
  protected Set<String> existingNames = new HashSet<>();

  public GUINodeIdTrafo() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTGUICompilationUnit ast) {
    collectNamesMode = true;
    ast.accept(getTraverser());
    collectNamesMode = false;
    ast.accept(getTraverser());
  }

  @Override
  public void visit(ASTGUIComponent node) {
    if (collectNamesMode) {
      if (node.isPresentId()) {
        existingNames.add(node.getId().getName());
      }
    } else if (!node.isPresentId()) {
      String name;
      do {
        name = StringTransformations.uncapitalize(node.getName()) + counter++;
      } while (existingNames.contains(name));
      node.setId(
          GUIDSLMill.gUIVariableDeclarationBuilder()
              .setName(name)
              .build()
      );
    }
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (collectNamesMode) {
      if (node.isPresentId()) {
        existingNames.add(node.getId().getName());
      }
    } else if (!node.isPresentId()) {
      String name;
      do {
        name = "it" + counter++;
      } while (existingNames.contains(name));
      node.setId(
          GUIDSLMill.gUIVariableDeclarationBuilder()
              .setName(name)
              .build()
      );
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    if (collectNamesMode) {
      if (node.isPresentId()) {
        existingNames.add(node.getId().getName());
      }
    } else if (!node.isPresentId()) {
      String name;
      do {
        name = "guard" + counter++;
      } while (existingNames.contains(name));
      node.setId(
          GUIDSLMill.gUIVariableDeclarationBuilder()
              .setName(name)
              .build()
      );
    }
  }

}

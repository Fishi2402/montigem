/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;

import java.util.ArrayList;
import java.util.List;

public class GUIComponentStat implements GUIDSLVisitor2 {

  protected static GUIComponentStat instance;
  protected GUIDSLTraverser traverser;

  protected List<ASTGUIComponent> components = new ArrayList<>();
  protected List<ASTGUIGuard> guards = new ArrayList<>();
  protected List<ASTGUIIterate> iterates = new ArrayList<>();

  public static GUIComponentStat getInstance() {
    if (instance == null) {
      instance = new GUIComponentStat();
    }
    return instance;
  }

  protected GUIComponentStat() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public List<ASTGUIComponent> getComponents(ASTGUIComponentDeclaration node) {
    components.clear();
    node.accept(getTraverser());
    return components;
  }

  public List<ASTGUIGuard> getGuards(ASTGUIComponentDeclaration node) {
    guards.clear();
    node.accept(getTraverser());
    return guards;
  }

  public List<ASTGUIIterate> getIterates(ASTGUIComponentDeclaration node) {
    iterates.clear();
    node.accept(getTraverser());
    return iterates;
  }

  @Override
  public void visit(ASTGUIComponent node) {
    components.add(node);
  }

  @Override
  public void visit(ASTGUIGuard node) {
    guards.add(node);
  }

  @Override
  public void visit(ASTGUIIterate node) {
    iterates.add(node);
  }

  public ASTGUIComponent getCompById(List<ASTGUIComponent> components, String id) {
    for (ASTGUIComponent c : components) {
      if (c.isPresentId() && c.getId().getName().equals(id)) {
        return c;
      }
    }
    return null;
  }

  public ASTGUIIterate getIterById(List<ASTGUIIterate> iterates, String id) {
    for (ASTGUIIterate c : iterates) {
      if (c.isPresentId() && c.getId().getName().equals(id)) {
        return c;
      }
    }
    return null;
  }

  public ASTGUIGuard getGuardById(List<ASTGUIGuard> guards, String id) {
    for (ASTGUIGuard c : guards) {
      if (c.isPresentId() && c.getId().getName().equals(id)) {
        return c;
      }
    }
    return null;
  }

  public ASTGUINamedArg getArgByName(ASTGUIComponent comp, String name) {
    for (ASTGUINamedArg arg : comp.getGUINamedArgs().getGUINamedArgList()) {
      if (name.equals(arg.getName())) {
        return arg;
      }
    }
    return null;
  }

}

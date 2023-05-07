/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._ast.ASTIterateControl;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GUIIterate2ArgsVisitor implements GUIDSLVisitor2 {

  protected GUIDSLTraverser traverser;
  protected ASTGUIComponent node;

  protected boolean active = false;

  protected List<String> args = new ArrayList<>();
  Deque<ASTGUIIterate> iterateStack = new ArrayDeque<>();

  public GUIIterate2ArgsVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public List<String> getArgs() {
    return args;
  }

  public void transform(ASTGUICompilationUnit ast, ASTGUIComponent node) {
    this.node = node;
    ast.accept(getTraverser());

    while (!iterateStack.isEmpty()) {
      ASTGUIIterate it = iterateStack.pop();
      ASTIterateControl ic = it.getIterateControl();
      if (ic instanceof ASTGUIShortIterateControl) {
        ASTGUIShortIterateControl control = (ASTGUIShortIterateControl) ic;
        String arg = "";
        // TODO AGe: type checker must set type properly
        if (control.getItem().isPresentSymbol()
            && control.getItem().getSymbol().getType() != null) {
          arg += control.getItem().getSymbol().getType().print();
        } else {
          if (control.isPresentMCType()) {
            arg += control.getMCType().printType();
          }
          else {
            Log.error("0x00006 type of " + control.getItem().getName()
                + " of iterate " + node.getId().getName()
                + " has to be set explicitly. Type derivation is not yet supported");
          }
          /*
          Log.error("0x00006 iterate item " + control.getItem().getName()
              + " of iterate " + node.getId().getName()
              + " is missing symbol");
              /**/
        }
        arg += " " + control.getItem().getName();
        args.add(arg);
      }
      else {
        Log.error("0x00004 iterate construct " + node.getId().getName()
            + " was not processed: range iterate control not supported yet");
      }
    }
  }

  @Override
  public void visit(ASTGUIComponent node) {
    if (this.node.equals(node)) {
      active = true;
    }
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (!active) {
      iterateStack.push(node);
    }
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (!active) {
      iterateStack.pop();
    }
  }

}

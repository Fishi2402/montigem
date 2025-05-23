package demorec.processmodeling;

import de.monticore.bpmn.workflow._ast.ASTFlowBlock;
import de.monticore.bpmn.workflow._ast.ASTFlowBranch;
import de.monticore.bpmn.workflow._ast.ASTFlowTarget;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.prettyprint.IndentPrinter;

import java.util.HashMap;

public class InstructionsPrinter implements WorkflowVisitor2 {
  IndentPrinter printer;
  HashMap<String, String> endEffectorsByCategory;
  HashMap<String, HashMap<String, String>> tags;
  boolean oneBranchWasVisited;

  public InstructionsPrinter(HashMap<String, HashMap<String, String>> tags, HashMap<String, String> endEffectorsByCategory) {
    printer = new IndentPrinter();
    this.endEffectorsByCategory = endEffectorsByCategory;
    this.tags = tags;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void visit(ASTFlowBlock node) {
    oneBranchWasVisited = false;
  }

  public void endVisit(ASTFlowBranch node) {
    oneBranchWasVisited = true;
  }

  public void visit(ASTProcess node) {
    StringBuilder lineBuilder = new StringBuilder();
    lineBuilder.append("function ");
    lineBuilder.append(node.getName());
    lineBuilder.append(" () {\n");
    printer.println(lineBuilder.toString());
  }

  public void endVisit(ASTProcess node) {
    printer.println("}");
  }

  public void visit(ASTFlowTarget node) {
    if (!oneBranchWasVisited && node.isPresentNodeRef()) {
      String nodeName = node.getNodeRef().toString();
      if (tags.containsKey(nodeName)) {
        printer.println("// Task: " + nodeName);
        printer.println("equip_endeffector(\"" + tags.get(nodeName).get(ProcessModelTagger.taskEndEffectorTagName) + "\");");
        printer.println("move_to_position(\"" + tags.get(nodeName).get(ProcessModelTagger.taskJoinConnection3DPositionTagName) + "\");");
        printer.println("dissolve_" + endEffectorsByCategory.getOrDefault(tags.get(nodeName).get(ProcessModelTagger.taskEndEffectorTagName), "manual") + "_connection();\n");
      }
    }
  }
}

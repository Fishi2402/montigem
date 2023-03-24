/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos.teaching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._util.CDAssociationTypeDispatcher;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CDAssociationsAreNotCyclicAtLeastOne implements CDBasisASTCDDefinitionCoCo {

  Map<String, List<String>> graph = new HashMap<>();

  CDAssociationTypeDispatcher dispatcher = new CDAssociationTypeDispatcher();

  @Override
  public void check(ASTCDDefinition node) {

    for (ASTCDAssociation assoc: node.getCDAssociationsList()) {
      if (dispatcher.isASTCDRightToLeftDir(assoc.getCDAssocDir()) || dispatcher.isASTCDBiDir(assoc.getCDAssocDir())) {
        if (assoc.getLeft().isPresentCDCardinality() && (dispatcher.isASTCDCardOne(assoc.getLeft().getCDCardinality()) ||
                dispatcher.isASTCDCardAtLeastOne(assoc.getLeft().getCDCardinality()))) {
          List<String> adj = graph.getOrDefault(assoc.getRight().getMCQualifiedType().getMCQualifiedName().getQName(), new ArrayList<>());
          adj.add(assoc.getLeft().getMCQualifiedType().getMCQualifiedName().getQName());
          graph.put(assoc.getRight().getMCQualifiedType().getMCQualifiedName().getQName(), adj);
        }
      }
      if (dispatcher.isASTCDLeftToRightDir(assoc.getCDAssocDir()) || dispatcher.isASTCDBiDir(assoc.getCDAssocDir())) {
        if (assoc.getRight().isPresentCDCardinality() && (dispatcher.isASTCDCardOne(assoc.getRight().getCDCardinality()) ||
                dispatcher.isASTCDCardAtLeastOne(assoc.getRight().getCDCardinality()))) {
          List<String> adj = graph.getOrDefault(assoc.getLeft().getMCQualifiedType().getMCQualifiedName().getQName(), new ArrayList<>());
          adj.add(assoc.getRight().getMCQualifiedType().getMCQualifiedName().getQName());
          graph.put(assoc.getLeft().getMCQualifiedType().getMCQualifiedName().getQName(), adj);
        }
      }

      if (isCyclic()) {
        Log.error("0x52567: Associations must not form a dependency loop.");
      }
    }
  }

  protected boolean isCyclic() {
    List<String> visited = new ArrayList<>();
    List<String> stack = new ArrayList<>();

    for (String node: graph.keySet()) {
      if (isCyclic(node, visited, stack)) {
        return true;
      }
    }
    return false;
  }

  protected boolean isCyclic(String node, List<String> visited, List<String> stack) {
    if (stack.contains(node)) {
      removeFromGraph(stack);
      return true;
    }
    if (visited.contains(node)) {
      return false;
    }
    visited.add(node);
    stack.add(node);
    List<String> adj = graph.getOrDefault(node, new ArrayList<>());
    for (String s: adj) {
      if (isCyclic(s, visited, stack)) {
        removeFromGraph(stack);
        return true;
      }
    }
    stack.remove(node);
    return false;
  }

  protected void removeFromGraph(List<String> list) {
    for (int i = 0; i < list.size()-1; i++) {
      graph.get(list.get(i)).remove(list.get(i+1));
    }
  }

}

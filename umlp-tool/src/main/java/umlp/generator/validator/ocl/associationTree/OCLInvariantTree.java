package umlp.generator.validator.ocl.associationTree;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.ocl.ocl._ast.ASTOCLInvariant;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import org.gradle.internal.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OCLInvariantTree {

  private final ASTOCLInvariant invariant;

  private final List<OCLInvariantTreeNode> oCLTreeNodes;

  public OCLInvariantTree(ASTOCLInvariant invariant) {
    this.invariant = invariant;
    this.oCLTreeNodes = new ArrayList<>();
  }

  public ASTOCLInvariant getInvariant() {
    return this.invariant;
  }

  public List<OCLInvariantTreeNode> getBranchRootNodes() {
    return this.oCLTreeNodes;
  }

  private void addRootNode(OCLInvariantTreeNode oCLTreeNode) {
    oCLTreeNode.setTreeLevel(0);
    this.oCLTreeNodes.add(oCLTreeNode);
  }

  public Optional<OCLInvariantTreeNode> findNode(ASTOCLParamDeclaration declaration) {
    return findNode(declaration.getName(), declaration.getEnclosingScope());
  }

  public Optional<OCLInvariantTreeNode> findNode(String declarationName, IBasicSymbolsScope enclosingScope) {
    for(OCLInvariantTreeNode node : getBranchRootNodes()) {
      Optional<OCLInvariantTreeNode> nodeToAddTo = findNode(node, declarationName, enclosingScope);
      if(nodeToAddTo.isPresent()) {
        return nodeToAddTo;
      }
    }
    return Optional.empty();
  }


  private Optional<OCLInvariantTreeNode> findNode(OCLInvariantTreeNode oCLCallTreeNode, String declarationName, IBasicSymbolsScope enclosingScope) {
    if(oCLCallTreeNode.getParameterDeclaration().isPresent() &&
        oCLCallTreeNode.getParameterDeclaration().get().getEnclosingScope().resolveVariable(declarationName).equals(enclosingScope.resolveVariable(declarationName)) &&
        oCLCallTreeNode.getParameterDeclaration().get().getName().equals(declarationName)) {
      return Optional.of(oCLCallTreeNode);
    }
    for(OCLInvariantTreeNode node : oCLCallTreeNode.getChildren()) {
      Optional<OCLInvariantTreeNode> result = findNode(node, declarationName, enclosingScope);
      if(result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }

  public void addNode(OCLInvariantTreeNode node) {
    if(node.getParameterDeclaration().isEmpty()) {
      throw new RuntimeException("Cannot add a node without a parameter declaration.");
    }
    ASTOCLParamDeclaration declaration = node.getParameterDeclaration().get();
    Optional<OCLInvariantTreeNode> nodeToMergeTo = findNode(declaration);
    if(nodeToMergeTo.isPresent()) {
      for(OCLInvariantTreeNode child : node.getChildren()) {
        if(nodeToMergeTo.get().getChildren().stream().noneMatch(n -> n.matches(child))) {
          nodeToMergeTo.get().addChild(child);
        } else {
          nodeToMergeTo.get().getMatchingChild(child).ifPresent(n -> n.mergeNode(child));
        }
      }
    } else {
      addRootNode(node);
    }
  }

  public Optional<OCLInvariantTreeNode> findMatchingBranch(List<String> call, IBasicSymbolsScope enclosingScope) {
    if (call.isEmpty()) {
      return Optional.empty();
    }
    Optional<OCLInvariantTreeNode> matchingNode = findNode(call.get(0), enclosingScope);
    OCLInvariantTreeNode result;

    if(matchingNode.isPresent()) {
      result = matchingNode.get().shallowClone();
      List<String> remainingCall = call.subList(1, call.size());
      if(remainingCall.isEmpty()) {
        return Optional.ofNullable(result);
      }
      for(OCLInvariantTreeNode child : matchingNode.get().getChildren()) {
        Optional<OCLInvariantTreeNode> matchingChild = child.findMatchingBranch(remainingCall);
        matchingChild.ifPresent(result::addChild);
      }
      return Optional.of(result);
    }

    return Optional.empty();
  }
}

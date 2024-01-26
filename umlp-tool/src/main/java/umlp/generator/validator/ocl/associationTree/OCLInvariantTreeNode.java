package umlp.generator.validator.ocl.associationTree;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.ocl.ocl._ast.ASTOCLParamDeclaration;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OCLInvariantTreeNode {
  private final List<OCLInvariantTreeNode> children = new ArrayList<>();

  private final ASTOCLParamDeclaration parameterDeclaration;

  private final ASTCDAssociation association;
  private final String attributeName;
  private int treeLevel;

  private OCLInvariantTreeNode(ASTOCLParamDeclaration parameterDeclaration, ASTCDAssociation association, String attributeName) {
    this.parameterDeclaration = parameterDeclaration;
    this.association = association;
    this.attributeName = attributeName;
  }

  public OCLInvariantTreeNode(ASTCDAssociation association) {
    this(null, association, null);
  }

  public OCLInvariantTreeNode(ASTOCLParamDeclaration parameterDeclaration) {
    this(parameterDeclaration, null, null);
  }

  public OCLInvariantTreeNode(String attributeName) {
    this(null, null, attributeName);
  }

  public List<OCLInvariantTreeNode> getChildren() {
    return this.children;
  }

  public void addChild(OCLInvariantTreeNode child) {
    if(!this.children.contains(child)) {
      child.setTreeLevel(this.treeLevel + 1);
      this.children.add(child);
    }
  }

  public int getTreeLevel() {
    return treeLevel;
  }

  public void setTreeLevel(int treeLevel) {
    this.treeLevel = treeLevel;
  }

  public Optional<ASTOCLParamDeclaration> getParameterDeclaration() {
    return Optional.ofNullable(this.parameterDeclaration);
  }

  public Optional<ASTCDAssociation> getAssociation() {
    return Optional.ofNullable(association);
  }

  public Optional<String> getAttributeName() {
    return Optional.ofNullable(this.attributeName);
  }

  public boolean hasParameterDeclaration() {
    return getParameterDeclaration().isPresent();
  }

  public boolean hasAssociation() {
    return getAssociation().isPresent();
  }

  public boolean hasAttributeName() {
    return getAttributeName().isPresent();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj instanceof OCLInvariantTreeNode) {
      OCLInvariantTreeNode other = (OCLInvariantTreeNode) obj;
      boolean isEqual = this.treeLevel == other.treeLevel;
      isEqual &= this.parameterDeclaration == other.parameterDeclaration;
      isEqual &= this.association == other.association;
      isEqual &= this.hasAttributeName() && other.hasAttributeName() && this.attributeName.equals(other.attributeName);
      isEqual &= this.children.equals(other.children);
      return isEqual;
    }
    return false;
  }

  public Set<String> getFinishingCalls(boolean printValidationResult, String conditionName) {
    Set<String> calls = new HashSet<>();
    String currentCall = "";
    if(hasParameterDeclaration()) {
      currentCall += this.parameterDeclaration.getName() + (printValidationResult ? "ValidationResult" : "");
    }
    if(children.isEmpty()) {
      return getFinishingCalls(currentCall, printValidationResult, calls, conditionName);
    } else {
      for (OCLInvariantTreeNode child : this.children) {
        calls.addAll(child.getFinishingCalls(currentCall, printValidationResult, calls, conditionName));
      }
    }
    return calls;
  }

  private Set<String> getFinishingCalls(String currentCall, boolean printValidationResult, Set<String> calls, String conditionName) {
    if(hasAssociation()) {
      String associationName = StringTransformations.capitalize(this.association.getName());
      if(this.association.getLeft().getMCQualifiedType().printType().equals(this.association.getRight().getMCQualifiedType().printType())) {
        associationName = "Right" + associationName;
      }
      if (this.association.getRight().getCDCardinality().isMult()) {
        return calls;
      } else {
        currentCall += ".get" + associationName + (printValidationResult ? "ValidationResult()" : "");
      }
    } else if(hasParameterDeclaration()) {
      if(this.parameterDeclaration.isPresentExpression()) {
        // TODO: implement
      }
      return calls;
    }
    if(isLeaf()) {
      if(hasAttributeName()) {
        currentCall += printValidationResult ? ".add" : ".get";
        currentCall += StringTransformations.capitalize(this.attributeName);
        currentCall += printValidationResult ? "ValidationMessage(" + conditionName + " ? ValidationMessage.ok() : ValidationMessage.error(message));" : "();";
        calls.add(currentCall);
      }
      return calls;
    }
    for(OCLInvariantTreeNode child : this.children) {
      calls.addAll(child.getFinishingCalls(currentCall, printValidationResult, calls, conditionName));
    }
    return calls;
  }

  public Set<String> getAssociationChain() {
    return getAssociationChain("", new HashSet<>());
  }

  private Set<String> getAssociationChain(String currentCall, Set<String> calls) {
    if(hasAssociation()) {
      if(!currentCall.isEmpty()) {
        currentCall += ".";
      }
      currentCall += this.association.getName();
    }
    if(isLeaf()) {
      calls.add(currentCall);
      return calls;
    }
    for(OCLInvariantTreeNode child : this.children) {
      calls.addAll(child.getAssociationChain(currentCall, calls));
    }
    return calls;
  }

  protected boolean thisOrChildrenAreRelatedToDomainClass(ASTCDType domainClass) {
    if(isRelatedToDomainClass(domainClass)) {
      return true;
    }
    if(isLeaf()) {
      return false;
    }
    return this.children.stream().anyMatch(child -> child.thisOrChildrenAreRelatedToDomainClass(domainClass));
  }

  protected boolean isRelatedToDomainClass(ASTCDType domainClass) {
    return isRelatedToDomainClassByDeclaration(domainClass) || isRelatedToDomainClassByAssociations(domainClass);
  }

  protected boolean isRelatedToDomainClassByDeclaration(ASTCDType domainClass) {
    return hasParameterDeclaration() && this.parameterDeclaration.getMCType().printType().equals(domainClass.getName());
  }

  protected boolean isRelatedToDomainClassByAssociations(ASTCDType domainClass) {
    return hasAssociation() && this.association.getRight().getMCQualifiedType().printType().equals(domainClass.getName());
  }

  public boolean isLeaf() {
    return this.children.isEmpty();
  }

  public boolean isRoot() {
    return this.treeLevel == 0;
  }

  protected boolean matches(OCLInvariantTreeNode other) {
    return hasParameterDeclaration() && other.hasParameterDeclaration() && other.getParameterDeclaration().get().equals(this.parameterDeclaration)
        || hasAssociation() && other.hasAssociation() && other.getAssociation().get().equals(this.association)
        || hasAttributeName() && other.hasAttributeName() && other.getAttributeName().get().equals(this.attributeName);
  }

  protected boolean matches(String name) {
    return hasParameterDeclaration() && this.parameterDeclaration.getName().equals(name)
        || hasAssociation() && this.association.getRight().getName().equals(name)
        || hasAttributeName() && this.attributeName.equals(name);
  }

  public Optional<OCLInvariantTreeNode> findDeepestNodeForCall(List<String> call) {
    if(call.isEmpty()) {
      return Optional.of(this);
    }
    if(matches(call.get(0))) {
      if(isLeaf() || call.size() == 1) {
        return Optional.of(this);
      }
      for(OCLInvariantTreeNode child : this.children) {
        Optional<OCLInvariantTreeNode> deepestNode = child.findDeepestNodeForCall(call.subList(1, call.size()));
        if(deepestNode.isPresent()) {
          return deepestNode;
        }
      }
    }
    return Optional.empty();
  }

  public OCLInvariantTreeNode findDeepestNodeForCallBranch() {
    if(this.children.size() > 1) {
      throw new IllegalStateException("Cannot find deepest node for branch representing a call if there are multiple children.");
    } else if(isLeaf()) {
      return this;
    }
    return this.children.get(0).findDeepestNodeForCallBranch();
  }

  protected OCLInvariantTreeNode shallowClone() {
    OCLInvariantTreeNode clone = new OCLInvariantTreeNode(this.parameterDeclaration, this.association, this.attributeName);
    clone.setTreeLevel(this.treeLevel);
    return clone;
  }

  protected Optional<OCLInvariantTreeNode> findMatchingBranch(List<String> remainingCall) {
    if(remainingCall.isEmpty()) {
      return Optional.empty();
    }
    if(matches(remainingCall.get(0))) {
      OCLInvariantTreeNode clone = this.shallowClone();
      for(OCLInvariantTreeNode child : this.children) {
        if(child.matches(remainingCall.get(0))) {
          child.findMatchingBranch(remainingCall.subList(1, remainingCall.size())).ifPresent(clone::addChild);
        }
      }
      return Optional.of(clone);
    }
    return Optional.empty();
  }

  protected void mergeNode(OCLInvariantTreeNode other) {
    if(this.matches(other)) {
      for(OCLInvariantTreeNode otherChild : other.children) {
        if(this.children.stream().noneMatch(n -> n.matches(otherChild))) {
          this.children.add(otherChild);
        } else {
          getMatchingChild(otherChild).ifPresent(n -> n.mergeNode(otherChild));
        }
      }
    }
  }

  protected Optional<OCLInvariantTreeNode> getMatchingChild(OCLInvariantTreeNode node) {
    return this.children.stream().filter(n -> n.matches(node)).findFirst();
  }

  public int getLevelOfFirstAssociation() {
    if(this.hasAssociation()) {
      return this.getTreeLevel();
    } else if(!this.getChildren().isEmpty()) {
      Set<Integer> associationTreeLevels = new HashSet<>();
      for(OCLInvariantTreeNode child : this.getChildren()) {
        associationTreeLevels.add(child.getLevelOfFirstAssociation());
      }
      return Collections.min(associationTreeLevels);
    }
    return -1;
  }
}

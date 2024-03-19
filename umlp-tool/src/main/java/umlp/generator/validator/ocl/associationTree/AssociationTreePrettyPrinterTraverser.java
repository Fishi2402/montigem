package umlp.generator.validator.ocl.associationTree;

import java.util.Collections;
import java.util.List;

public class AssociationTreePrettyPrinterTraverser {

    private final OCLInvariantTree oCLInvariantTree;

    private final List<AssociationTreePrettyPrintingVisitor> associationTreePrettyPrintingVisitors;

    public AssociationTreePrettyPrinterTraverser(OCLInvariantTree oCLInvariantTree, AssociationTreePrettyPrintingVisitor associationTreePrettyPrintingVisitors) {
      this(oCLInvariantTree, Collections.singletonList(associationTreePrettyPrintingVisitors));
    }

    public AssociationTreePrettyPrinterTraverser(OCLInvariantTree oCLInvariantTree, List<AssociationTreePrettyPrintingVisitor> associationTreePrettyPrintingVisitors) {
      this.oCLInvariantTree = oCLInvariantTree;
      this.associationTreePrettyPrintingVisitors = associationTreePrettyPrintingVisitors;
    }

    public OCLInvariantTree getAssociationTree() {
      return oCLInvariantTree;
    }

    public void traverse() {
      associationTreePrettyPrintingVisitors.forEach(a -> a.visit(oCLInvariantTree));
      for(OCLInvariantTreeNode node : oCLInvariantTree.getBranchRootNodes()) {
        associationTreePrettyPrintingVisitors.forEach(a -> a.visit(node));
        traverseAssociationTreeNode(node);
        associationTreePrettyPrintingVisitors.forEach(a -> a.endVisit(node));
      }
      associationTreePrettyPrintingVisitors.forEach(a -> a.endVisit(oCLInvariantTree));
    }

    private void traverseAssociationTreeNode(OCLInvariantTreeNode treeNode) {
      for(OCLInvariantTreeNode node : treeNode.getChildren()) {
        associationTreePrettyPrintingVisitors.forEach(a -> a.visit(node));
        traverseAssociationTreeNode(node);
        associationTreePrettyPrintingVisitors.forEach(a -> a.endVisit(node));
      }
    }

  public List<String> prettyPrint() {
    traverse();
    return associationTreePrettyPrintingVisitors.stream().map(AssociationTreePrettyPrintingVisitor::getResult).collect(java.util.stream.Collectors.toList());
  }
}

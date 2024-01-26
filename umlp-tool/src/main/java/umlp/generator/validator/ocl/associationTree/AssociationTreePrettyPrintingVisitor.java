package umlp.generator.validator.ocl.associationTree;

public interface AssociationTreePrettyPrintingVisitor {
  void visit(OCLInvariantTree node);
  void visit(OCLInvariantTreeNode node);

  void endVisit(OCLInvariantTree node);

  void endVisit(OCLInvariantTreeNode node);
  String getResult();
}

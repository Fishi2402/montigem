package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

public interface OCLAttachableState extends VisitorState {
  List<OCLStatement> oclStatements();
}

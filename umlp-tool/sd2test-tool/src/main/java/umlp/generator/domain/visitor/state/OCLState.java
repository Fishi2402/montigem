package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

public interface OCLState extends VisitorState {
  List<OCLStatement> oclStatements();
}

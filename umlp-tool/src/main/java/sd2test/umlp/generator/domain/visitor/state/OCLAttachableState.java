package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

public interface OCLAttachableState extends VisitorState {
  List<OCLStatement> oclStatements();
}

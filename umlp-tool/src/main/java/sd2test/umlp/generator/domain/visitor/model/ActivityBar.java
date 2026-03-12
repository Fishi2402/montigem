package sd2test.umlp.generator.domain.visitor.model;

import sd2test.umlp.generator.domain.visitor.model.interaction.Interaction;
import sd2test.umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class ActivityBar {
  private final List<
      Interaction> body = new ArrayList<>();
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public List<Interaction> body() {
    return body;
  }

  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public String toString() {
    return "ActivityBar{" + "body=" + body + ", oclStatements=" + oclStatements + '}';
  }
}

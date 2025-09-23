package umlp.generator.domain.visitor.model;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SDTest implements SDTestExtractable {
  private final List<ASTMCImportStatement> imports = new ArrayList<>();
  private Optional<MatchingStrategy> matchingStrategy = Optional.empty();
  private final List<SDObject> sdObjects = new ArrayList<>();
  private final List<OCLStatement> preInteractionOCLStatements = new ArrayList<>();
  private final List<Trigger> trigger = new ArrayList<>();

  public List<ASTMCImportStatement> imports() {
    return imports;
  }

  public Optional<MatchingStrategy> matchingStrategy() {
    return matchingStrategy;
  }

  public SDTest matchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = Optional.of(matchingStrategy);
    return this;
  }

  public List<SDObject> sdObjects() {
    return sdObjects;
  }

  public List<OCLStatement> preInteractionOCLStatements() {
    return preInteractionOCLStatements;
  }

  public List<Trigger> trigger() {
    return trigger;
  }

  @Override
  public String toString() {
    return "SDTest{" + "imports=" + imports + ", matchingStrategy=" + matchingStrategy +
           ", sdObjects=" + sdObjects + ", preInteractionOCLStatements=" +
           preInteractionOCLStatements + ", trigger=" + trigger + '}';
  }

  @Override
  public SDTest extractTest() {
    return this;
  }
}

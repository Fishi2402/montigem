package umlp.generator.domain.visitor.model;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class SDTest implements SDTestExtractable {
  private final List<ASTMCImportStatement> imports = new ArrayList<>();
  private final List<SDObject> sdObjects = new ArrayList<>();
  private final List<OCLStatement> preInteractionOCLStatements = new ArrayList<>();
  private final List<Trigger> trigger = new ArrayList<>();

  public List<ASTMCImportStatement> imports() {
    return imports;
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
    return "SDTest{" + "imports=" + imports + ", sdObjects=" + sdObjects +
           ", preInteractionOCLStatements=" + preInteractionOCLStatements + ", trigger=" + trigger +
           '}';
  }

  @Override
  public SDTest extractTest() {
    return this;
  }
}

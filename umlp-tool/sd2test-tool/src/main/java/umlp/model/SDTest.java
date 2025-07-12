package umlp.model;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction of a test described by a sequence diagram
 */
@Deprecated
public final class SDTest {
  /**
   * Import statements required for the test
   */
  private final List<ASTMCImportStatement> importStatements = new ArrayList<>();
  /**
   * All objects that need to be created for the test and their modifiers
   */
  private final List<SDObject> objectDeclarations = new ArrayList<>();
  /**
   * Some initial conditions and checks that should evaluate to true before the first trigger
   */
  private final List<String> initialChecks = new ArrayList<>();
  /**
   * All triggers and the events that are to be expected from them
   */
  private final List<Trigger> triggers = new ArrayList<>();

  public void addImportStatement(ASTMCImportStatement statement) {
    importStatements.add(statement);
  }

  public void addObjectDeclaration(SDObject object) {
    objectDeclarations.add(object);
  }

  public void addInitialCheck(String check) {
    initialChecks.add(check);
  }

  public void addTrigger(Trigger trigger) {
    triggers.add(trigger);
  }

  public List<ASTMCImportStatement> getImportStatements() {
    return importStatements;
  }

  public List<SDObject> getObjectDeclarations() {
    return objectDeclarations;
  }

  public List<String> getInitialChecks() {
    return initialChecks;
  }

  public List<Trigger> getTriggers() {
    return triggers;
  }
}

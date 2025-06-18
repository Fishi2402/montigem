package umlp.model;

import de.monticore.lang.sd4development._ast.ASTSDCall;

import java.util.ArrayList;
import java.util.List;

public final class Trigger {
  //TODO Is it relevant where the call is coming from? yes!

  /**
   * The name of the target where the trigger method is called on, i.e., a class name or the name
   * of a variable
   */
  private final String triggerTargetName;
  /**
   * The triggering call
   */
  private final ASTSDCall triggerCall;
  /**
   * All events that are expected to follow on the trigger call, in order
   */
  private final List<ExpectedEvent> expectedEvents = new ArrayList<>();

  public Trigger(String triggerTargetName, ASTSDCall triggerCall) {
    this.triggerTargetName = triggerTargetName;
    this.triggerCall = triggerCall;
  }
}

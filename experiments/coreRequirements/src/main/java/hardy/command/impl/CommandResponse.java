/* (c) https://github.com/MontiCore/monticore */

package hardy.command.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for the results of the processed commands
 */
public class CommandResponse {
  // the unique id of the response, to match with the sent commands
  private final long id;
  private final List<CommandResult> responses;

  public CommandResponse(long id) {
    this.id = id;
    this.responses = new ArrayList<>();
  }

  public long getId() {
    return id;
  }

  // TODO AHe: generics?

  public void addResponse(CommandResult response) {
    this.responses.add(response);
  }

  public List<CommandResult> getResults() {
    return this.responses;
  }

  public void clear() {
    this.responses.clear();
  }

}

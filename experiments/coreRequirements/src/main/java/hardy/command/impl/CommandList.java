/* (c) https://github.com/MontiCore/monticore */

package hardy.command.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for a command
 */
public class CommandList {
  /**
   * running (unique) id, to identify the sent commands
   */
  private long id;
  private List<Command> commands;

  public CommandList() {
    this.commands = new ArrayList<>();
  }

  public CommandList(List<Command> commands) {
    this.commands = commands;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Command> getCommands() {
    return commands;
  }

  public void setCommands(List<Command> commands) {
    this.commands = commands;
  }

  public void addCommand(Command command) {
    if (this.commands == null) {
      this.commands = new ArrayList<>();
    }

    this.commands.add(command);
  }
}

/* (c) https://github.com/MontiCore/monticore */

package hardy.command.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for a command
 */
public class CommandDTOList {
  /**
   * running (unique) id, to identify the sent commands
   */
  private long id;
  private List<CommandDTO> commands;

  public CommandDTOList() {
    this.commands = new ArrayList<>();
  }

  public CommandDTOList(List<CommandDTO> commands) {
    this.commands = commands;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<CommandDTO> getCommands() {
    return commands;
  }

  public void setCommands(List<CommandDTO> commands) {
    this.commands = commands;
  }

  public void addCommand(CommandDTO command) {
    if (this.commands == null) {
      this.commands = new ArrayList<>();
    }

    this.commands.add(command);
  }
}

/* (c) https://github.com/MontiCore/monticore */

package hardy.command.send;

import de.se_rwth.commons.logging.Log;
import hardy.command.dto.CommandDTO;
import hardy.dto.DTO;
import hardy.error.MontiGemError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * used to execute commands from the backend
 */
public class CommandCaller {

  private final List<CommandDTO> commands;
  private final List<CommandPromise> promises;
  private long id;

  public CommandCaller() {
    this.id = 0;
    this.commands = new ArrayList<>();
    this.promises = new ArrayList<>();
  }

  /**
   * add any command to the list
   *
   * @param cmd    any given command
   * @param method the callback method to use when given cmd is executed
   */
  public CommandCaller add(CommandDTO cmd, CommandCallback<DTO> method) {
    long id = this.id;
    this.id++;

    cmd.setCommandId(id);
    this.commands.add(cmd);
    this.promises.add(new CommandPromise(id, method));

    return this;
  }

  /**
   * Clear all the stored mappings
   */
  public void clear() {
    this.commands.clear();
    this.promises.clear();
    this.id = 0;
  }

  /**
   * Get the corresponding callback for the command commandId
   *
   * @param commandId
   * @return corresponding callback
   */
  private Optional<CommandCallback<DTO>> getById(long commandId) {
    return promises.stream().filter(promise -> promise.getId() == commandId).map(CommandPromise::getMethod)
      .findAny();
  }

  /**
   * execute all given commands locally
   *
   * @throws RuntimeException
   */
  public void execute() throws RuntimeException {
    this.commands.forEach(cmd -> {
      Optional<CommandCallback<DTO>> method = getById(cmd.getCommandId());
      if (!method.isPresent()) {
        Log.warn("MAB0xA100: could not find callback method for id " + cmd.getCommandId());
        throw new RuntimeException("MAB0xA100: could not find callback method for id " + cmd.getCommandId());
      }

      DTO d = cmd.doRun();
      method.get().onOk(d);
    });

    clear();
  }

  /**
   * Run the given command directly
   *
   * @param commandDTO any given command
   * @param <D>        the type of the result DTO
   * @return the result of the command execution
   * @throws ClassCastException when the result doesn't have the intended type
   * @throws MontiGemError      on any error in the command itself
   */
  public <D extends DTO> D runCommand(CommandDTO commandDTO)
    throws ClassCastException, MontiGemError {
    DTO dto = commandDTO.doRun();
    @SuppressWarnings("unchecked")
    D specificDTO = (D) dto;
    return specificDTO;
  }
}


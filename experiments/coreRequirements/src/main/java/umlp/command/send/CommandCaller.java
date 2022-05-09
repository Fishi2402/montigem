/* (c) https://github.com/MontiCore/monticore */

// todo: Zweck unklar; Streichen?

package umlp.command.send;
// RTE-P
import de.se_rwth.commons.logging.Log;
import umlp.command.Command;
import hardy.error.MontiGemError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * used to execute commands from the backend
 */
public class CommandCaller {

  private final List<Command> commands;
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
  public CommandCaller add(Command cmd, CommandCallback<Object> method) {
    long id = this.id;
    this.id++;

    cmd.setId(id);
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
  private Optional<CommandCallback<Object>> getById(long commandId) {
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
      Optional<CommandCallback<Object>> method = getById(cmd.getId());
      if (!method.isPresent()) {
        Log.warn("MAB0xA100: could not find callback method for id " + cmd.getId());
        throw new RuntimeException("MAB0xA100: could not find callback method for id " + cmd.getId());
      }

      Object d = cmd.doRun();
      method.get().onOk(d);
    });

    clear();
  }

  /**
   * Run the given command directly
   *
   * @param command any given command
   * @param <D>        the type of the result domain object
   * @return the result of the command execution
   * @throws ClassCastException when the result doesn't have the intended type
   * @throws MontiGemError      on any error in the command itself
   */
  public <D> D runCommand(Command command)
    throws ClassCastException, MontiGemError {
    Object domainObject = command.doRun();
    @SuppressWarnings("unchecked")
    D specificDomainObject = (D) domainObject;
    return specificDomainObject;
  }
}


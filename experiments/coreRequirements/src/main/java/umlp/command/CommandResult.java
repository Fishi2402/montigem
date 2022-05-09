/* (c) https://github.com/MontiCore/monticore */

package umlp.command;
// RTE-P

// todo: erläutern

import hardy.error.MontiGemError;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import java.util.Optional;

/**
 * single result of an executed command
 */
public class CommandResult<T> {
  private long commandId;
  private Optional<Gem<T>> gem = Optional.empty();
  private final boolean forGemCommand;
  private Optional<MontiGemError> error = Optional.empty();

  private CommandResult(long id, MontiGemError error, boolean forGemCommand) {
    this.commandId = id;
    this.forGemCommand = forGemCommand;
    this.error = Optional.of(error);
    // TODO AHe: use information from error
    this.gem = Optional.of(new Gem<T>(id, LoadingState.FAILED_TO_LOAD));
  }

  private CommandResult(long id, T domainObject) {
    this.commandId = id;
    this.forGemCommand = true;
    this.gem = Optional.of(new Gem<T>(domainObject));
  }

  private CommandResult(long id) {
    this.commandId = id;
    this.forGemCommand = false;
  }


/* 
  // todo:
  wieso ist es so relevant, ob es ein "domänenobjekt" oder einen anderen
  Datensatz returniert?
  Ich kann den Bedarf hierzu noch nicht sehen?
*/

  /**
   * Successful CommandResult of a command that does not return a domain object
   * @param commandId id of the command
   * @return a CommandResult without a gem and a successful status
   */
  public static CommandResult<Void> ok(long commandId){
    return new CommandResult<Void>(commandId);
  }

  /**
   * Successful CommandResult of a command that does return a domain object
   * @param commandId id of the command
   * @return a CommandResult containing gem and a successful status
   */
  public static <T> CommandResult<T> gemOk(long commandId, T domainObject){
    return new CommandResult<T>(commandId, domainObject);
  }

  /**
   * Unsuccessful CommandResult of a command that should not return a domain object
   * @param commandId if of the command
   * @param error error describing the failure
   * @return a CommandResult with an unsuccessful status
   */
  public static CommandResult<Void> error(long commandId, MontiGemError error){
    return new CommandResult<Void>(commandId, error, false);
  }

  /**
   * Unsuccessful CommandResult of a command that should return a domain object
   * @param commandId if of the command
   * @param error error describing the failure
   * @return a CommandResult with an unsuccessful status
   */
  public static <T> CommandResult<T> gemError(long commandId, MontiGemError error){
    return new CommandResult<T>(commandId, error, true);
  }


  public Optional<Gem<T>> getGem() {
    return gem;
  }

  public void setGem(Gem<T> gem) {
    this.gem = Optional.of(gem);
  }

  public long getCommandId() {
    return commandId;
  }

  public void setCommandId(long commandId) {
    this.commandId = commandId;
  }

  public boolean wasSuccessful(){
    return !error.isPresent();
  }

  public MontiGemError getError(){
    return error.get();
  }

  public boolean isForGemCommand(){
    return forGemCommand;
  }
}

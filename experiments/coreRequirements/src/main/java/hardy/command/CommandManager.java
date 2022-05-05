/* (c) https://github.com/MontiCore/monticore */

package hardy.command;

import de.se_rwth.commons.logging.Log;
import hardy.command.impl.Command;
import hardy.command.impl.CommandList;
import hardy.command.impl.CommandResponse;
import hardy.command.impl.CommandResult;
import hardy.error.MontiGemError;
import hardy.error.MontiGemErrorFactory;
import hardy.error.NotImplementedException;

import static hardy.error.MontiGemErrorCode.NOT_IMPLEMENTED;

// TODO AHe: as Mill?
public class CommandManager {

  // TODO AHe: problem for generics: CommandList can contain commands of multiple types
  /**
   * run all given commands
   *
   * @param commandList a complete list of commands to run
   * @return a combined response for all the command results
   */
  public static CommandResponse runCommands(CommandList commandList) {
    // create an empty response with the id to identify the response on the other end
    CommandResponse response = new CommandResponse(commandList.getId());
    // EntityTransaction transaction = em.getTransaction();

    Log.debug("MAB0xA003: Start transaction", CommandManager.class.getName());

    long cmdId = -1;

    try {
      // iterate all the commands
      for (Command command : commandList.getCommands()) {
        cmdId = command.getId();
        DomainObject result;
        try {
          result = command.doRun();
          if(command.isReturnsGem()) {
            response.addResponse(CommandResult.gemOk(command.getId(), result));
          }else{
            response.addResponse(CommandResult.ok(command.getId()));
          }
        }
        catch (NotImplementedException e) {
          response.addResponse(CommandResult.error(command.getId(), new MontiGemError(NOT_IMPLEMENTED, null, "Mathod not implemented")));
        }
      }

      // transaction.commit();
      Log.debug("MAB0xA004: Transaction commited", CommandManager.class.getName());
    }
    catch (NullPointerException np) {
      response.clear();
      response.addResponse(CommandResult.error(cmdId, MontiGemErrorFactory.unknown("" + np + ": " + np.getStackTrace()[0])));
      Log.debug("MAB0xA005: Error in transaction: " + np.getMessage(), CommandManager.class.getName());
    }
    catch (MontiGemError e) {
      // transaction.rollback();
      response.clear();
      response.addResponse(CommandResult.error(cmdId, e));
      Log.debug("MAB0xA006: Error in transaction: " + e.getMessage(), CommandManager.class.getName());
    }
    catch (Exception e) {
      // transaction.rollback();
      response.clear();
      response.addResponse(CommandResult.error(cmdId, MontiGemErrorFactory.exceptionCaught(e)));
      Log.debug("MAB0xA007: Error in transaction: " + e.getMessage(), CommandManager.class.getName());
    }

    return response;
  }

  /**
   * deserialize and run commands
   *
   * @param jsonCmd all the commands as json
   * @return the result of the command executions
   */
  // TODO AHe: impl
  /*public static CommandResponseDTO manage(String jsonCmd) { // TODO SVa: have the deserialization outside, just handle CommandDTOList?
    CommandDTOList commandList;
    Log.debug("MAB0xA000: Manage commands: '" + jsonCmd + "'", CommandManager.class.getName());

    try {
      // deserialize the command
      commandList = JsonMarshal.getInstance().unmarshal(jsonCmd, CommandDTOList.class);
    }
    catch (JsonException e) {
      Log.debug("MAB0xA001: Error: " + e.getMessage(), CommandManager.class.getName());
      return new CommandResponseDTO(-1, MontiGemErrorFactory.deserializeError(e.getMessage() + ": " + jsonCmd));
    }

    if (commandList == null) {
      Log.error("MAB0xA002: Empty Command List");
      return new CommandResponseDTO(-1, MontiGemErrorFactory.resolverError("Empty command list: " + jsonCmd));
    }

    return runCommands(commandList);
  }*/
}

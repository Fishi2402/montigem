/* (c) https://github.com/MontiCore/monticore */

/*
  // todo: to be handled:
a) expand CommandList
b) Erklärungen hinzufügen
  
*/ 

package umlp.command;
// RTE-P
// RTE-T

import de.se_rwth.commons.logging.Log;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorFactory;
import umlp.command.error.NotImplementedException;

import static umlp.command.error.MontiGemErrorCode.NOT_IMPLEMENTED;

// TODO AHe: Als Mill, sodass das Verhalten im Front und Backend simuliert werden kann? Bei E2E soll die Kommunikation
// nicht getestet werden, daher aktuell noch nicht notwendig
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
        try {
          response.addResponse(command.doRun());
        }
        catch (NotImplementedException e) {
          response.addResponse(StatusResult.error(cmdId, new MontiGemError(NOT_IMPLEMENTED, null, "Mathod not implemented")));
        }
      }

      // transaction.commit();
      Log.debug("MAB0xA004: Transaction commited", CommandManager.class.getName());
    }
    catch (NullPointerException np) {
      response.clear();
      response.addResponse(StatusResult.error(cmdId, MontiGemErrorFactory.unknown("" + np + ": " + np.getStackTrace()[0])));
      // todo: Wie kann eine Fehlermeldung "Error" ...vi "Debug rausgehen ..."????
      Log.debug("MAB0xA005: Error in transaction: " + np.getMessage(), CommandManager.class.getName());
    }
    catch (MontiGemError e) {
      // transaction.rollback();
      response.clear();
      response.addResponse(StatusResult.error(cmdId, e));
      // todo: Wie kann eine Fehlermeldung "Error" ...vi "Debug rausgehen ..."????
      Log.debug("MAB0xA006: Error in transaction: " + e.getMessage(), CommandManager.class.getName());
    }
    catch (Exception e) {
      // transaction.rollback();
      response.clear();
      response.addResponse(StatusResult.error(cmdId, MontiGemErrorFactory.exceptionCaught(e)));
      // todo: Wie kann eine Fehlermeldung "Error" ...vi "Debug rausgehen ..."????
      Log.debug("MAB0xA007: Error in transaction: " + e.getMessage(), CommandManager.class.getName());
    }

    return response;
  }

  public static <T> CommandResult<T> executeSingleCommand(Command<T> command) {
    return command.doRun();
  }

  protected static long freeCommandId;
  // TODO: Kann das setzten von commandIds intern vom CommandManager übernommen werden?
  public static long getFreeCommandId(){
    return freeCommandId++;
  }


  /**
   * deserialize and run commands
   *
   * @param jsonCmd all the commands as json
   * @return the result of the command executions
   */
  // TODO AHe: impl
  // todo: also das Deserialisieren von Objekten ist definitiv nicht hier anzuseiedeln!
  // Aus sicht des Datenstransfers sind Commands ja auch nichts anderes als Daten, und daher 
  // dortz zu behandeln, wo die Kommunikation sitzt
  
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

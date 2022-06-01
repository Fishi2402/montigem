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

// nicht getestet werden, daher aktuell noch nicht notwendig
public class CommandManager {
  protected CommandManager(){ }

  protected static CommandManager instance;

  public static void init(){
    instance = new CommandManager();
  }

  public static void initMe(CommandManager commandManager){
    instance = commandManager;
  }

  public static CommandManager getInstance() {
    if(instance == null){
      instance = new CommandManager();
    }
    return instance;
  }

  /**
   * run all given commands
   *
   * @param commandList a complete list of commands to run
   * @return a combined response for all the command results
   */
  public static CommandResponse runCommands(CommandList commandList) {
    return getInstance()._runCommands(commandList);
  }

  protected CommandResponse _runCommands(CommandList commandList) {
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
    return getInstance()._executeSingleCommand(command);
  }

  protected  <T> CommandResult<T> _executeSingleCommand(Command<T> command) {
    return command.doRun();
  }

  protected static long freeCommandId;
  // TODO: Kann das setzten von commandIds intern vom CommandManager übernommen werden?
  public static long getFreeCommandId(){
    return getInstance()._getFreeCommandId();
  }

  protected long _getFreeCommandId() {
    return freeCommandId++;
  }
}

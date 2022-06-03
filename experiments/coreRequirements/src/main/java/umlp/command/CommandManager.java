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
import umlp.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
  // Id dieses Clients/Servers
  // Server ist immer 0
  protected long participantId = 0;

  protected CommandManager(){ }
  // Speichert alle verbundenen Clients und deren Ids, wird genutzt um die Commands die relevante objekte bearbeiten
  // weiter zu verteilen.
  protected Map<Long, CommandSubscription> subscribers = new HashMap<>();
  protected long runningClientId = 1;

  protected static CommandManager instance;

  public static long subscribe(CommandSubscription subscription){
    return getInstance()._subscribe(subscription);
  }

  public static long getParticipantId() {
    return getInstance()._getParticipantId();
  }

  protected long _getParticipantId(){
    return participantId;
  }

  protected long _subscribe(CommandSubscription subscription) {
    long id = runningClientId++;
    subscribers.put(id, subscription);
    return id;
  }

  // TODO: unsubscribe

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

  public static <T> CommandResult<T> executeSingleCommand(Command<T> command) {
    Log.trace("Exec command " + command.getId() + " " + command.getCommandType() + " from " + command.getParticipantId(), LogUtil.getLoggerName());
    return getInstance()._executeSingleCommand(command);
  }

  protected  <T> CommandResult<T> _executeSingleCommand(Command<T> command) {
    CommandResult<T> res = command.doRun();
    // TODO: error handling if doRun fails
    if(!command.isRequest()) {
      for (Map.Entry<Long, CommandSubscription> e : subscribers.entrySet()) {
        if (!e.getKey().equals(command.getParticipantId())) {
          Log.trace("Sending command " + command.getId() + " " + command.getCommandType() + " to " + e.getKey(), LogUtil.getLoggerName());
          e.getValue().sendCommand(command);
        }
      }
    }
    return res;
  }

  protected static long freeCommandId;
  public static long getFreeCommandId(){
    return getInstance()._getFreeCommandId();
  }

  protected long _getFreeCommandId() {
    return freeCommandId++;
  }
}

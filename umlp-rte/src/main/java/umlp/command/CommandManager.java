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
  protected Map<Long, Participant> particpants = new HashMap<>();
  protected long runningClientId = 1;

  protected static CommandManager instance;

  public static long register(Participant participant){
    return getInstance()._register(participant);
  }

  public static void deregister(long participantId) {
    getInstance()._deregister(participantId);
  }

  public static long getParticipantId() {
    return getInstance()._getParticipantId();
  }

  protected long _getParticipantId(){
    return participantId;
  }

  public long _register(Participant participant) {
    long participantId = runningClientId++;
    particpants.put(participantId, participant);
    return participantId;
  }

  protected void _deregister(long participantId) {
    particpants.remove(participantId);
  }


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

  public static <T> CommandResult<T> executeCommand(Command<T> command) {
    Log.trace("Exec command " + command.getId() + " " + command.getCommandType() + " from " + command.getParticipantId(), LogUtil.getLoggerName());
    Log.trace(umlp.command.CommandObjectMapper.serializeCommand(command), LogUtil.getLoggerName());
    return getInstance()._executeCommand(command);
  }

  protected  <T> CommandResult<T> _executeCommand(Command<T> command) {
    CommandResult<T> res = command.doRun();
    // update subscribed gemIds
    if(particpants.containsKey(command.getParticipantId())){
      particpants.get(command.getParticipantId()).subscribe(command.getNewlySubscribedGemIds());
    } else {
      // TODO: MH: kann das passieren?
    }
    // TODO: error handling if doRun fails
    if(!command.isRequest()) {
      _sendCommandToSubscribers(command);
    }
    return res;
  }

  public static <T> void sendCommandToSubscribers(Command<T> command){
    getInstance()._sendCommandToSubscribers(command);
  }

  protected <T> void _sendCommandToSubscribers(Command<T> command) {
    for (Long participantId : particpants.keySet()) {
      Participant participant = particpants.get(participantId);
      if (!participantId.equals(command.getParticipantId())) {
        boolean match = false;
        for (Long gemId : command.getEffectedGemIds()) {
          if (participant.isSubscribedTo(gemId)) {
            match = true;
            break;
          }
        }
        if(match) {
          Log.trace("Sending command " + command.getId() + " " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
          participant.sendCommand(command);
        }else{
          Log.trace("Skip sending command " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
        }
      }
    }
  }

  protected long freeCommandId;
  public static long getFreeCommandId(){
    return getInstance()._getFreeCommandId();
  }

  protected long _getFreeCommandId() {
    return freeCommandId++;
  }
}

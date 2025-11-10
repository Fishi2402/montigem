/* (c) https://github.com/MontiCore/monticore */

/*
  // todo: to be handled:
a) expand CommandList
b) Erklärungen hinzufügen

*/

package umlp.backendrte.command;
// RTE-P
// RTE-T

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.util.LogUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
  // Id dieses Clients/Servers
  // Server ist immer 0
  protected long participantId = 0;

  protected CommandManager(){ }
  // Speichert alle verbundenen Clients und deren Ids, wird genutzt um die Commands die relevante objekte bearbeiten
  // weiter zu verteilen.
  protected Map<Long, Participant> particpants = new ConcurrentHashMap<>();
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
    participant.setParticipantId(participantId);
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
    Log.trace(CommandObjectMapper.serializeCommand(command), LogUtil.getLoggerName());
    return getInstance()._executeCommand(command);
  }

  protected  <T> CommandResult<T> _executeCommand(Command<T> command) {
    if(command.checkAccessPolicy() && command.checkContract()) {
      Optional<Participant> participantOpt = Optional.ofNullable(particpants.get(command.getParticipantId()));
      if (command.getParticipantId() != 0 && participantOpt.isEmpty()) {
        // participantID 0 is the default (backend)
        throw new IllegalStateException("Unexpected state: Participant " + command.getParticipantId() + " is unknown!");
      }
      CommandResult<T> res = command.doRun();
      // update subscribed gemIds
      participantOpt.ifPresent(p -> p.subscribe(command.getNewlySubscribedGemIds()));
      // TODO: error handling if doRun fails
      if (!command.isRequest()) {
        _sendCommandToSubscribers(command);
      }
      return res;
    }else{
      return (CommandResult<T>) StatusResult.error(command.getId());
    }
  }

  public static <T> void sendCommandToSubscribers(Command<T> command){
    getInstance()._sendCommandToSubscribers(command);
  }

  protected <T> void _sendCommandToSubscribers(Command<T> command) {
    // Copy current participants to avoid ConcurrentModificationException if a new participant is
    // added during this loop.
    // A newly added participant has not yet subscribed to gemIds or types and thus the current
    // command is not relevant for it.
    Set<Long> paricipantIds;
    synchronized (particpants) {
      paricipantIds = new HashSet<>(particpants.keySet());
    }

    for (Long participantId : paricipantIds) {
      Participant participant = particpants.get(participantId);
      if (!participantId.equals(command.getParticipantId())) {
        boolean match = shouldSendCommand(command, participant);
        if(match) {
          System.out.println("Sending command " + command.getId() + " " + command.getCommandType() + " to " + participantId);
          // If the command send because of the type filter, the id needs to be subscribed
          participant.subscribe(command.getEffectedGemIds());
          participant.sendCommand(command);
        }else{
          Log.trace("Skip sending command " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
        }
      }
    }
  }

  public static <T> boolean shouldSendCommand(Command<T> command, Participant participant) {
    return getInstance()._shouldSendCommand(command, participant);
  }

  protected  <T> boolean _shouldSendCommand(Command<T> command, Participant participant) {
    for (Long gemId : command.getEffectedGemIds()) {
      if (participant.isSubscribedTo(gemId)) {
        return true;
      }
    }

    return participant.isSubscribedToType(command.getCommandType());
  }

  public static void subscribeToType(long participantId, DomainTypeFilter domainTypeFilter){
    getInstance()._subscribeToType(participantId, domainTypeFilter);
  }

  protected void _subscribeToType(long participantId, DomainTypeFilter domainTypeFilter) {
    var p = particpants.get(participantId);
    if(p != null){
      p.subscribeType(domainTypeFilter);
    }
  }

  protected long freeCommandId;
  public static long getFreeCommandId(){
    return getInstance()._getFreeCommandId();
  }

  protected long _getFreeCommandId() {
    return freeCommandId++;
  }

  public Participant getParticipant(long id) {
    return particpants.get(id);
  }
}

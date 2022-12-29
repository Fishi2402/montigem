/* (c) https://github.com/MontiCore/monticore */
package umlp.command;

// RTE-P

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Ausbaustufe: nicht alle Commands versenden, nur für Objekte die der Client geladen hat
public class Participant {
  protected Set<Long> gemIds = new HashSet<>();
  protected ServerCommandCommunication commandCommunication;

  public Participant(ServerCommandCommunication commandCommunication) {
    this.commandCommunication = commandCommunication;
  }

  public <T> void sendCommand(Command<T> command){
    commandCommunication.sendCommandToParticipant(command);
  }

  // Überprüft, ob dieser Client updates zum Datensatz mit der übergebenen gemId erhalten muss.
  public boolean isSubscribedTo(long gemId){
    return gemIds.contains(gemId);
  }

  public void subscribe(Collection<Long> gemIds){
    this.gemIds.addAll(gemIds);
  }
}

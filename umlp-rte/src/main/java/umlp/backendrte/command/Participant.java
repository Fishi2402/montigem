/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

// RTE-P

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Ausbaustufe: nicht alle Commands versenden, nur für Objekte die der Client geladen hat
public class Participant {
  protected Set<Long> gemIds = new HashSet<>();
  protected ServerCommandCommunication commandCommunication;
  protected Set<DomainTypeFilter> domainTypeFilters = new HashSet<>();

  protected String username;

  public Participant(ServerCommandCommunication commandCommunication) {
    this.commandCommunication = commandCommunication;
    this.username = "";
  }

  public Participant(ServerCommandCommunication commandCommunication, String username) {
    this.commandCommunication = commandCommunication;
    this.username = username;
  }

  public <T> void sendCommand(Command<T> command){
    commandCommunication.sendCommandToParticipant(command);
  }

  // Überprüft, ob dieser Client updates zum Datensatz mit der übergebenen gemId erhalten muss.
  public boolean isSubscribedTo(long gemId){
    return gemIds.contains(gemId);
  }

  public boolean isSubscribedToType(String typeId){
    return domainTypeFilters.stream().anyMatch(d -> d.test(typeId));
  }

  public void subscribe(Collection<Long> gemIds){
    this.gemIds.addAll(gemIds);
  }

  public void subscribeType(DomainTypeFilter filter){
    domainTypeFilters.add(filter);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}

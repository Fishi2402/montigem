package hardy.commands.person;

// todo: Bitte verstehen, dies ist nicht: // RTE-P, sondern:
// GEN-P

// todo: hardy.commands.person.Person_create   ist suboptimal, alternativen:
// hardy.command.Person_create  
// (analog dann die anderen Kommandos anpassen)

import hardy.FlensburgManager;
import hardy.Person;
import hardy.command_infrastructure.impl.Command;
import hardy.error.MontiGemError;

/*
// todo: Anforderungen für Commands (vor allem auch welche) mit BR besprechen

    Ich sehe folgende Anforderungen als relevant an (BR):
    * Objekte (zum Beispiel) können erzeugt und gelöscht werden
        --> Erzeugung: ist wie eine Factory? 
        Oder gibt es das Objekt schon intern und es wird nur gespeichert?
    * Attribute in Objekten können geändert werden
        (z.B. Person_set_age, Person_addCar, Person_+ListOperator+Car)
    * und Attribute können gelesen werden (wobei ich nicht davon überzeugt bin, dass hier ein Command notwendig ist)
        ((z.B. Person_get_age, Person_addCar, Person_)

    --> dies bitte adäquat abbilden
    --> dann Rücksprache BR

*/

import java.util.Optional;

public class Person_create extends Command {

  protected final Person person;

  // todo: implementierung? oder zumindest mal erklären, was das Command nun wirklich macht
  protected Person_create(long commandId, Person person) {
    super(
        commandId,
        "hardy.Person",
        false
    );

    this.person = person;
  }

  @Override
  public Optional<Void> doRun() throws MontiGemError {
    FlensburgManager.addPerson(person);
    return Optional.empty();
  }

  // TODO AHe: undo

}

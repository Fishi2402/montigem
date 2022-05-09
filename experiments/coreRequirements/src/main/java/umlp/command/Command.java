/* (c) https://github.com/MontiCore/monticore */

package umlp.command;
// RTE-P

import de.se_rwth.commons.logging.Log;
import umlp.command.error.MontiGemError;
import umlp.command.error.NotImplementedException;

import java.util.Optional;

public abstract class Command {
  // Der Ersteller des Commands vergibt diese id und nutzt sie nachher um die Response des Backends, welche dieselbe id enthält, diesem Command zuzuordnen.
  // Ansonsten können parallel ankommende Responses nicht eindeutig ihren Commands zugeordnet werden.
  private long id;
  
  // Enthielt in MontiGem das feld typeName, welches den Typ des zurückgegebenen commands enthielt.
  // Hier nicht unbedingt notwendig, wenn die Subklassen sich selber um (De)-Serialisierung kümmern

  // Falls die Response zu diesem Command keine Daten liefert, ist returnsGem false.
  // Eingebaut um den früheren OkDTO zu ersetzten.
  private final boolean returnsGem;

  // TODO AHe: returnsGem und typeName können weggelassen werden, wenn die (De)-Serialisierung verschoben wird, z.B. wenn sie im Command liegt, so ist der Returntyp bekannt und kann dort gecasted werden.
  // cont: Dann hat allerdings die Klasse Command mehrere Aufgaben: (De)-Serialisierung, Ausführung

  protected Command(long id, boolean returnsGem) {
    this.id = id;
    this.returnsGem = returnsGem;
  }

  /**
   *   Must return an object iff isReturnsGem returns true and the command was executed without exceptions.
   *   Returns Optional.empty if the Response should not contain an object.
   *   TODO AHe: Warum dann ein Optional zurückgeben? isReturnsGem == false könnte einen return von null implizieren
   *   This method must always be overridden
   */
  public abstract Optional<?> doRun() throws MontiGemError;

  public void undoRun() {
    String res = "MAB0x9000: No undo for operation " + getClass().getName() + "available";

// todo: ist das der Umgang den wir mit Undos vornehmen?
// Warnung einerseits an den User ist ja schön, aber
// Exception andererseits heisst ja, dass das System in einen unerwarteten Fehler
// eingetreten ist (und im Batch-Fall jetzt terminieren sollte)
// Was eigentlich für Log.error spricht.
// Andererseits sind wir wahrscheinlich interaktiv unterwegs, da wäre eine
// einfache Warnung gg. schon ausreichend?
// --> Rücksprache BR was da ein sinnvolles Verhalten ist
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public long getId() {
    return id;
  }

  // TODO AHe: a DTO was returned here, how about a boolean?
  // TODO AHe: What is this used for?
  // protected abstract Optional<Object> checkContract();

  public boolean isReturnsGem() {
    return returnsGem;
  }

  public void setId(long id) {
    this.id = id;
  }
}

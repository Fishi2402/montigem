// Der Typenparameter T gibt an, welchen Datentyp gewrapped in ein CommandResult<T> ein Command zurückgeben soll.
// Falls es nur eine Status-Meldung ist, so wird T = Void genutzt und der Status aus dem CommandResult ausgelesen.
// Bei Human_get wäre T = Human, sodass der Entwickler den Human aus dem CommandResult auslesen kann, oder am Status
// erkennt, das ein Fehler aufgetreten ist.
import {CommandManager} from "./command.manager";
import {NotImplementedException} from "./error/NotImplementedException";
import {CommandResult} from "./command.result";

export abstract class Command<T> {
  // Der Ersteller des Commands vergibt diese id und nutzt sie nachher um die Response des Backends, welche dieselbe id enthält, diesem Command zuzuordnen.
  // Ansonsten können parallel ankommende Responses nicht eindeutig ihren Commands zugeordnet werden.
  private id: number;

  // Id des Clients der diesen Command erstellt hat.
  // Hiermit wird sichergestellt, dass ein Client keine Commands zurückgeschickt bekommt, die von ihm erstellt wurden
  private participantId: number;

  // Identifiziert den typen des Commands
  // Wird bei der Deserialisierung benötigt um die korrekte Subklasse von Command zu instanziieren
  private readonly commandType: string;

  protected constructor(commandType: string) {
    this.id = CommandManager.getFreeCommandId();
    this.participantId = CommandManager.getParticipantId();
    this.commandType = commandType;
  }

  public abstract doRun(): CommandResult<T>;


// TODO: undo kann nicht immer ausgeführt werden.
// Erste idee: Der FlensburgManager merkt sich zu jedem Objekt, welche Commands es als letztes
// beeinflusst haben(inkl. Assoziationen) und nur die letzte Änderung kann rückgängig gemacht werden.
// Als Stack könnten mehrere Änderungen eines Clients rückgängig gemacht werden, so lange kein anderer Client
// einen Command ausführt der die betroffenen Objekte bearbeitet.
  public undoRun(): void {
    let res = "MAB0x9000: No undo for operation " + this.constructor.name + " available";

// todo: ist das der Umgang den wir mit Undos vornehmen?
// Warnung einerseits an den User ist ja schön, aber
// Exception andererseits heisst ja, dass das System in einen unerwarteten Fehler
// eingetreten ist (und im Batch-Fall jetzt terminieren sollte)
// Was eigentlich für Log.error spricht.
// Andererseits sind wir wahrscheinlich interaktiv unterwegs, da wäre eine
// einfache Warnung gg. schon ausreichend?
// --> Rücksprache BR was da ein sinnvolles Verhalten ist
    // TODO Log.warn(res);
    throw new NotImplementedException(res);
  }

  public getId(): number {
    return this.id;
  }

  public setId(id: number): void {
    this.id = id;
  }

  public getCommandType(): string {
    return this.commandType;
  }

  public getParticipantId(): number {
    return this.participantId;
  }

// Ein request verändert die Daten nicht und muss daher nicht an andere Clients weitergeleitet werden
// z.B Human_get.
// Um die technische Umsetzung zu vereinfachen, nutzen Requests dieselbe Infrastruktur wie Commands
  public isRequest(): boolean {
    return false;
  }


// GemIds, der Daten welcher dieser Command verändert
// TODO: für alle Commands implementieren
  public getEffectedGemIds(): Set<number> {
    return new Set<number>();
  }

// GemIds, zu welchen dieser Client updates erhalten muss
  public getNewlySubscribedGemIds(): Set<number> {
    return this.getEffectedGemIds();
  }
}

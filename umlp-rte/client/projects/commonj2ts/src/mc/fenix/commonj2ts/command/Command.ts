/* (c) https://github.com/MontiCore/monticore */
import {NotImplementedException} from './error/NotImplementedException';
import {CommandResult} from './CommandResult';
import {CommandManager} from './CommandManager';
import {Log} from "../logging";
import {LoadingState} from "../common";
import {SimpleResult} from "./SimpleResult";

// Der Typenparameter T gibt an, welchen Datentyp gewrapped in ein CommandResult<T> ein Command zurückgeben soll.
// Falls es nur eine Status-Meldung ist, so wird T = Void genutzt und der Status aus dem CommandResult ausgelesen.
// Bei Human_get wäre T = Human, sodass der Entwickler den Human aus dem CommandResult auslesen kann, oder am Status
// erkennt, das ein Fehler aufgetreten ist.
export abstract class Command<T> {
  // Der Ersteller des Commands vergibt diese id und nutzt sie nachher um die Response des Backends, welche dieselbe id enthält, diesem Command zuzuordnen.
  // Ansonsten können parallel ankommende Responses nicht eindeutig ihren Commands zugeordnet werden.
  private id: number;

  // Id des Clients der diesen Command erstellt hat.
  // Hiermit wird sichergestellt, dass ein Client keine Commands zurückgeschickt bekommt, die von ihm erstellt wurden
  private participantId: number;

  // Identifiziert den typen des Commands
  // Wird bei der Deserialisierung benötigt um die korrekte Subklasse von Command zu instanziieren
  private commandType: string;

  constructor(commandType: string) {
    if (this.id === undefined) {
      this.id = 0;
    }
    if (this.participantId === undefined) {
      this.participantId = 0;
    }
    if (this.commandType === undefined) {
      this.commandType = null;
    }
    this.id = CommandManager.getFreeCommandId();
    this.participantId = CommandManager.getParticipantId();
    this.commandType = commandType;
  }

  public doRun(): Promise<CommandResult<T>> {
    if (!this.checkAccessPolicy()) {
      return Promise.resolve(<CommandResult<T>><unknown>new SimpleResult().createFromObject(this.getId(), LoadingState.NOT_AUTHORIZED, this.commandType));
    } else if (!this.checkContract()) {
      return Promise.resolve(<CommandResult<T>><unknown>new SimpleResult().createFromLoadingState(this.getId(), LoadingState.FAILED_TO_LOAD, this.commandType));
    } else {
      return Promise.resolve(this.doAction());
    }
  }

  public undoRun() {
    let res: string = "MAB0x9000: No undo for operation " + this.constructor.name + " available";
    Log.warn(res);
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

  // Um die technische Umsetzung zu vereinfachen, nutzen Requests dieselbe Infrastruktur wie Commands
  public isRequest(): boolean {
    return false;
  }


  // Ein request verändert die Daten nicht und muss daher nicht an andere Clients weitergeleitet werden
  // z.B Human_get.

  // GemIds, der Daten welcher dieser Command verändert
  public getEffectedGemIds(): Array<number> {
    return [];
  }

  // GemIds, zu welchen dieser Client updates erhalten muss
  public getNewlySubscribedGemIds(): Array<number> {
    return this.getEffectedGemIds();
  }

  public setParticipantId(participantId: number): void {
    this.participantId = participantId;
  }

  /**
   * Checks if the issuer of this command has the correct rights to execute it
   */
  public abstract checkAccessPolicy(): Promise<boolean>;

  /**
   * Checks if the preconditions for this command are met
   */
  public checkContract(): Promise<boolean> {
    return Promise.resolve(true);
  }

  /**
   * The action this commando performs.
   * Access rights checks should be implemented in {@link this#checkAccessPolicy()} and
   * preconditions in {@link this#checkContract()}.
   * Should not be called directly, instead use {@link this#doRun()}, which will perform the checks
   * before executing the action.
   */
  protected abstract doAction(): Promise<CommandResult<T>>;
}

Command["__class"] = "umlp.backendrte.command.Command";






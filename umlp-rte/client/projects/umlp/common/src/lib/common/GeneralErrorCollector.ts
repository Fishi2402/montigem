/* (c) https://github.com/MontiCore/monticore */

import { CommandManager } from "../command/CommandManager";
import { ServerError } from "../command/ServerError";
import { SyncError } from "../command/SyncError";
import { GeneralErrorObserver } from "./GeneralErrorObserver";

// RTE-P

// Wandelt generelle Fehler(z.B. Verbindungsfehler) in Fehlernachrichten um und verteilt sie an alle observer.
// Static delegate, daher ersetzbar, z.B. für unterschiedliche Texte fürs Log und Endnutzer
export class GeneralErrorCollector {
  protected static instance: GeneralErrorCollector;
  protected observers: Array<GeneralErrorObserver> = [];

  public static init(): void {
    this.instance = new GeneralErrorCollector();
  }

  public static getInstance(): GeneralErrorCollector {
    if (this.instance == null) {
      this.init();
    }
    return this.instance;
  }

  public static addErrorObserver(observer: GeneralErrorObserver): void {
    this.getInstance()._addErrorObserver(observer);
  }

  public static initMe(generalErrorCollector: GeneralErrorCollector): void {
    this.instance = generalErrorCollector;
  }

  protected _addErrorObserver(observer: GeneralErrorObserver): void {
    this.observers.push(observer);
  }

  public static containsErrorObserver(observer: GeneralErrorObserver): boolean {
    return this.getInstance()._containsErrorObserver(observer);
  }

  protected _containsErrorObserver(observer: GeneralErrorObserver): boolean {
    return this.observers.includes(observer);
  }

  public static removeErrorObserver(observer: GeneralErrorObserver): boolean {
    return this.getInstance()._removeErrorObserver(observer);
  }

  protected _removeErrorObserver(observer: GeneralErrorObserver): boolean {
    const index = this.observers.indexOf(observer);
    if (index >= 0) {
      this.observers.splice(index, 1);
      return true;
    }
    return false;
  }

  public static reportConnectionError(): void {
    this.getInstance()._reportConnectionError();
  }

  protected _reportConnectionError(): void {
    // Connection errors können nicht synchronisiert werden, die Verbindung ist nicht benutzbar!
    let msg: String = this._buildConnectionErrorMsg();
    this.observers.forEach(obs => obs.notifyConnectionError(msg));
  }

  protected _buildConnectionErrorMsg(): string {
    return "Connection error!";
  }

  // TODO AGe: Throwable
  public static reportServerError(t: any): void {
    this.getInstance()._reportServerError(t);
  }

  // TODO: Scoping, aktuell wird der ServerError an alle clients gesendet!
  // TODO AGe: Throwable
  protected _reportServerError(t: any): void {
    CommandManager.sendCommandToSubscribers(new ServerError().ServerError(t));
    let msg: string = this._buildServerErrorMsg(t);
    this.observers.forEach(obs => obs.notifyServerError(msg));
  }

  // TODO AGe: Throwable
  protected _buildServerErrorMsg(t: any): string {
    return "Server error: " + t.getMessage();
  }

  // TODO: Scoping, aktuell wird der SyncError an alle clients gesendet, ob wohl nur manche dieses Objekt interessiert
  public static reportSyncError(): void {
    this.getInstance()._reportSyncError();
  }

  protected _reportSyncError(): void {
    CommandManager.sendCommandToSubscribers(new SyncError().SyncError());
    let msg: String = this._buildSyncErrorMsg();
    this.observers.forEach(obs => obs.notifySyncError(msg));
  }

  protected _buildSyncErrorMsg(): String {
    return "Sync error!";
  }
}

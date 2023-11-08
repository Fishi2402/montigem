/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import {IUMLPList} from "../common";

export class Participant {
  gemIds: Array<number>;

  commandCommunication: ServerCommandCommunication;

  protected domainTypeFilters: DomainTypeFilter[] = [];

  protected username: string;

  public constructor(commandCommunication: ServerCommandCommunication, username?: string) {
    this.gemIds = <any>([]);
    if (this.commandCommunication === undefined) {
      this.commandCommunication = null;
    }
    this.commandCommunication = commandCommunication;
    this.username = "";
    if (username != undefined && username != null) {
      this.username = username;
    }
  }

  public sendCommand<T>(command: Command<T>) {
    this.commandCommunication.sendCommandToParticipant<any>(command);
  }

  public isSubscribedTo(gemId: number): boolean {
    return /* contains */(this.gemIds.indexOf(<any>(gemId)) >= 0);
  }

  public isSubscribedToType(typeId: string): boolean {
    for (let d of this.domainTypeFilters) {
      if (d.test(typeId)) {
        return true;
      }
    }
    return false;
  }

  public async subscribe(gemIds: Array<number> | IUMLPList<number>) {
    if (gemIds instanceof Array) {
      /* addAll */
      ((l1, l2) => l1.push.apply(l1, l2))(this.gemIds, gemIds);
    } else {
      this.gemIds.push(...await gemIds.toArray());
    }
  }

  public subscribeType(filter: DomainTypeFilter): void {
    this.domainTypeFilters.push(filter);
  }

  public getUsername(): string {
    return this.username;
  }

  public setUsername(username: string) {
    this.username = username;
  }

}

Participant["__class"] = "umlp.backendrte.command.Participant";


import {Command} from './Command';


import {ServerCommandCommunication} from './ServerCommandCommunication';
import {DomainTypeFilter} from "./DomainTypeFilter";


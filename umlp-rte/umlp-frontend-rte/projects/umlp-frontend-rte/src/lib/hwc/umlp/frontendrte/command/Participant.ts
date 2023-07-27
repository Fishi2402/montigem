/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
export class Participant {
    gemIds: Array<number>;

    commandCommunication: ServerCommandCommunication;

    public constructor(commandCommunication: ServerCommandCommunication) {
        this.gemIds = <any>([]);
        if (this.commandCommunication === undefined) { this.commandCommunication = null; }
        this.commandCommunication = commandCommunication;
    }

    public sendCommand<T>(command: Command<T>) {
        this.commandCommunication.sendCommandToParticipant<any>(command);
    }

    public isSubscribedTo(gemId: number): boolean {
        return /* contains */(this.gemIds.indexOf(<any>(gemId)) >= 0);
    }

    public subscribe(gemIds: Array<number>) {
        /* addAll */((l1, l2) => l1.push.apply(l1, l2))(this.gemIds, gemIds);
    }
}
Participant["__class"] = "umlp.backendrte.command.Participant";




import { Command } from './Command';


import { ServerCommandCommunication } from './ServerCommandCommunication';


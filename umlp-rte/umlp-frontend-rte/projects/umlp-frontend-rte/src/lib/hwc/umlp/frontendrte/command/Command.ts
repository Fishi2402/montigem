/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { NotImplementedException } from './error/NotImplementedException';
import { CommandResult } from './CommandResult';
import { CommandManager } from './CommandManager';
import {Log} from "../logging";

export abstract class Command<T> {
    /*private*/ id: number;

    /*private*/ participantId: number;

    /*private*/ commandType: string;

    constructor(commandType: string) {
        if (this.id === undefined) { this.id = 0; }
        if (this.participantId === undefined) { this.participantId = 0; }
        if (this.commandType === undefined) { this.commandType = null; }
        this.id = CommandManager.getFreeCommandId();
        this.participantId = CommandManager.getParticipantId();
        this.commandType = commandType;
    }

    public abstract doRun(): Promise<CommandResult<T>>;

    public undoRun() {
        const res: string = "MAB0x9000: No undo for operation " + /* getName */(c => typeof c === 'string' ? c : c["__class"] ? c["__class"] : c["name"])((<any>this.constructor)) + " available";
        Log.warn(res);
        throw new NotImplementedException(res);
    }

    public getId(): number {
        return this.id;
    }

    public setId(id: number) {
        this.id = id;
    }

    public getCommandType(): string {
        return this.commandType;
    }

    public getParticipantId(): number {
        return this.participantId;
    }

    public isRequest(): boolean {
        return false;
    }

    public getEffectedGemIds(): Array<number> {
        return /* emptySet */[];
    }

    public getNewlySubscribedGemIds(): Array<number> {
        return this.getEffectedGemIds();
    }

    public setParticipantId(participantId: number) {
        this.participantId = participantId;
    }
}
Command["__class"] = "umlp.backendrte.command.Command";






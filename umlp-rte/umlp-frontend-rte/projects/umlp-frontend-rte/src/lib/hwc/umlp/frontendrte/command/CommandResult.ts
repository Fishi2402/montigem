/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { IUMLPList } from '../common/IUMLPList';
import { StringHelper } from '../javautil/StringHelper';

/**
 * single result of an executed command
 * @class
 */
export abstract class CommandResult<T> {
    commandId: number;

    typeParameter: string;

    constructor(id: number, typeParameter: string) {
        if (this.commandId === undefined) { this.commandId = 0; }
        if (this.typeParameter === undefined) { this.typeParameter = null; }
        this.commandId = id;
        this.typeParameter = typeParameter;
    }

    public getCommandId(): number {
        return this.commandId;
    }

    public setCommandId(commandId: number) {
        this.commandId = commandId;
    }

    public isStatusResult(): boolean {
        return false;
    }

    public asStatusResult(): StatusResult {
        return this as unknown as StatusResult;
    }

    public isSimpleResult(): boolean {
        return false;
    }

    public asSimpleResult(): SimpleResult<T> {
        return this as unknown as SimpleResult<T>;
    }

    public getTypeParameter(): string {
        return this.typeParameter;
    }

    public isListResult(): boolean {
        return false;
    }

    public asListResult(): ListResult<T, IUMLPList<T>> {
        return <ListResult<T, IUMLPList<T>>><unknown>this;
    }
}
CommandResult["__class"] = "umlp.backendrte.command.CommandResult";




import { ListResult } from './ListResult';


import { SimpleResult } from './SimpleResult';


import { StatusResult } from './StatusResult';


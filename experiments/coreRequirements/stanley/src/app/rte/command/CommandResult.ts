/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace umlp.rte.command {
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

        public asStatusResult(): umlp.rte.command.StatusResult {
            return <umlp.rte.command.StatusResult>this;
        }

        public isSimpleResult(): boolean {
            return false;
        }

        public asSimpleResult(): umlp.rte.command.SimpleResult<T> {
            return <umlp.rte.command.SimpleResult<T>>this;
        }

        public getTypeParameter(): string {
            return this.typeParameter;
        }
    }

}


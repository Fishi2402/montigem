/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Result } from '../common/Result';
import { LogUtil } from '../util/LogUtil';
import { TestContext } from '../util/TestContext';

/**
 * Simulation der Kommunikation zwischen Client und Server auf direkter Ebene in einem Thread
 * Aktuell werden zum Ausführen eines Commands der Backend-Kontext wiederhergestellt,
 * und der Command ausgeführt.
 * Das result wird serialisiert und dann deserialisiert bevor es an den Client weitergeleitet wird.
 * Die (de)-serialisierung sorgt dafür, dass unterschiedliche Instanzen der verschickten Objekte erstellt werden,
 * wodurch sich die Datensynchronisierung testen lässt.
 * @param {*} backendContext
 * @class
 */
export class CommandDirectCommunication implements ClientCommandCommunication, ServerCommandCommunication {
    /* Default method injected from umlp.backendrte.command.ClientCommandCommunication */
    public onCommandReceived(observer: CommandReceiveObserver): ClientCommandCommunication {
        return this;
    }
    /* Default method injected from umlp.backendrte.command.ClientCommandCommunication */
    public onCommandResultReceived(observer: CommandResultReceiveObserver): ClientCommandCommunication {
        return this;
    }
    serverContext: TestContext;

    clientContext: TestContext;

    public constructor(backendContext: TestContext) {
        if (this.serverContext === undefined) { this.serverContext = null; }
        if (this.clientContext === undefined) { this.clientContext = null; }
        this.serverContext = backendContext;
    }

    public setClientContext(clientContext: TestContext) {
        this.clientContext = clientContext;
    }

    /**
     *
     * @param {Command} command
     */
    public async sendCommandToParticipant<T>(command: Command<T>) {
        this.clientContext.activate();
        const ser: string = await CommandObjectMapper.serializeCommand(command);
        const deser: Command<any> = /* get */(v => { if (v == null) throw new Error('value is null'); return v; })((await CommandObjectMapper.deserializeCommand(ser)));
        Log.trace(ser, LogUtil.getLoggerName());
        await deser.doRun();
        this.serverContext.activate();
    }

    /**
     *
     * @param {Command} command
     * @return {CommandResult}
     */
    public async sendCommandToServerAndReceiveResult<T>(command: Command<T>): Promise<CommandResult<T>> {
        const cmdStr: string = await CommandObjectMapper.serializeCommand(command);
        this.serverContext.activate();
        const recreatedCommand: Command<any> = /* get */(v => { if (v == null) throw new Error('value is null'); return v; })((await CommandObjectMapper.deserializeCommand(cmdStr)));
        const res: CommandResult<any> = await CommandManager.executeCommand<any>(recreatedCommand);
        const resStr: string = await CommandObjectMapper.serializeResponse(res);
        this.clientContext.activate();
        const cmdOpt: CommandResult<any> = await CommandObjectMapper.deserializeResponse(resStr);
        if (/* isEmpty */(cmdOpt == null)){
            Log.warn("Can not deser CommandResult from\n" + resStr);
        }
        const recreatedRes: CommandResult<any> = /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(cmdOpt);
        return <CommandResult<T>>recreatedRes;
    }

    /**
     *
     * @return {Result}
     */
    public async init(): Promise<Result<number>> {
        return Result.ok<any>((await this.serverContext.getCommandManager()._register(new Participant(this))));
    }
}
CommandDirectCommunication["__class"] = "umlp.backendrte.command.CommandDirectCommunication";
CommandDirectCommunication["__interfaces"] = ["umlp.backendrte.command.ClientCommandCommunication","umlp.backendrte.command.ServerCommandCommunication"];





import { CommandResultReceiveObserver } from './CommandResultReceiveObserver';


import { CommandReceiveObserver } from './CommandReceiveObserver';


import { Participant } from './Participant';


import { CommandManager } from './CommandManager';


import { CommandResult } from './CommandResult';


import { CommandObjectMapper } from './CommandObjectMapper';


import { Command } from './Command';


import { ServerCommandCommunication } from './ServerCommandCommunication';


import { ClientCommandCommunication } from './ClientCommandCommunication';
import {Log} from "../logging";


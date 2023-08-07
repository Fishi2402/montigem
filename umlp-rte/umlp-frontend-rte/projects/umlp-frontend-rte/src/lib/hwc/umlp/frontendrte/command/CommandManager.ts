/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Log } from '../logging/Log';
import { LogUtil } from '../util/LogUtil';

export class CommandManager {
    participantId: number;

    constructor() {
        this.participantId = 0;
        this.particpants = <any>({});
        this.runningClientId = 1;
        if (this.freeCommandId === undefined) { this.freeCommandId = 0; }
    }

    particpants: any;

    runningClientId: number;

    static instance: CommandManager = null;

    public static async register(participant: Participant): Promise<number> {
        return await CommandManager.getInstance()._register(participant);
    }

    public static deregister(participantId: number) {
        CommandManager.getInstance()._deregister(participantId);
    }

    public static getParticipantId(): number {
        return CommandManager.getInstance()._getParticipantId();
    }

    _getParticipantId(): number {
        return this.participantId;
    }

    public async _register(participant: Participant): Promise<number> {
        const participantId: number = this.runningClientId++;
        /* put */((m,k,v) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key==null && k==null || m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { m.entries[i].value=v; return; } m.entries.push({key:k,value:v,getKey: function() { return this.key }, getValue: function() { return this.value }}); })(<any>this.particpants, participantId, participant);
        return participantId;
    }

    async _deregister(participantId: number) {
        /* remove */((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key==null && k==null || m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return m.entries.splice(i,1)[0]; } })(<any>this.particpants, participantId);
    }

    public static init() {
        CommandManager.instance = new CommandManager();
    }

    public static initMe(commandManager: CommandManager) {
        CommandManager.instance = commandManager;
    }

    public static getInstance(): CommandManager {
        if (CommandManager.instance == null){
            CommandManager.instance = new CommandManager();
        }
        return CommandManager.instance;
    }

    public static async executeCommand<T>(command: Command<T>): Promise<CommandResult<T>> {
        Log.trace("Exec command " + command.getId() + " " + command.getCommandType() + " from " + command.getParticipantId(), LogUtil.getLoggerName());
        Log.trace((await CommandObjectMapper.serializeCommand(command)), LogUtil.getLoggerName());
        return await CommandManager.getInstance()._executeCommand<any>(command);
    }

    async _executeCommand<T>(command: Command<T>): Promise<CommandResult<T>> {
        const res: CommandResult<T> = await command.doRun();
        if (/* containsKey */((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key==null && k==null || m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return true; } return false; })(<any>this.particpants, command.getParticipantId())){
            /* get */((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key==null && k==null || m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return m.entries[i].value; } return null; })(<any>this.particpants, command.getParticipantId()).subscribe(command.getNewlySubscribedGemIds());
        } else {
        }
        if (!command.isRequest()){
            this._sendCommandToSubscribers<any>(command);
        }
        return res;
    }

    public static sendCommandToSubscribers<T>(command: Command<T>) {
        CommandManager.getInstance()._sendCommandToSubscribers<any>(command);
    }

    async _sendCommandToSubscribers<T>(command: Command<T>) {
        {
            let array = /* keySet */((m) => { let r=[]; if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) r.push(m.entries[i].key); return r; })(<any>this.particpants);
            for(let index = 0; index < array.length; index++) {
                let participantId = array[index];
                {
                    const participant: Participant = /* get */((m,k) => { if(m.entries==null) m.entries=[]; for(let i=0;i<m.entries.length;i++) if(m.entries[i].key==null && k==null || m.entries[i].key.equals!=null && m.entries[i].key.equals(k) || m.entries[i].key===k) { return m.entries[i].value; } return null; })(<any>this.particpants, participantId);
                    if (!(participantId === command.getParticipantId())){
                        let match: boolean = false;
                        {
                            let array1 = command.getEffectedGemIds();
                            for(let index1 = 0; index1 < array1.length; index1++) {
                                let gemId = array1[index1];
                                {
                                    if (participant.isSubscribedTo(gemId)){
                                        match = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (match){
                            Log.trace("Sending command " + command.getId() + " " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
                            participant.sendCommand<any>(command);
                        } else {
                            Log.trace("Skip sending command " + command.getCommandType() + " to " + participantId, LogUtil.getLoggerName());
                        }
                    }
                }
            }
        }
    }

    freeCommandId: number;

    public static getFreeCommandId(): number {
        return CommandManager.getInstance()._getFreeCommandId();
    }

    _getFreeCommandId(): number {
        return this.freeCommandId++;
    }
}
CommandManager["__class"] = "umlp.backendrte.command.CommandManager";




import { CommandObjectMapper } from './CommandObjectMapper';


import { Command } from './Command';


import { CommandResult } from './CommandResult';


import { Participant } from './Participant';


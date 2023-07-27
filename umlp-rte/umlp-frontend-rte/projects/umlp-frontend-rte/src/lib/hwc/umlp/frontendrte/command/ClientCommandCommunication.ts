/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Result } from '../common/Result';

/**
 * Simuliert die Kommunikation zwischen Client und Server
 * @class
 */
export interface ClientCommandCommunication {
    /**
     * Sendet ein Command an den Server und wartet auf das Ergebnis
     * @param {Command} command
     * @return {CommandResult}
     */
    sendCommandToServerAndReceiveResult<T>(command: Command<T>): Promise<CommandResult<T>>;

    /**
     * Initiiert die Verbindung und liefert die vom Server verteilte Participant Id
     * @return {Result}
     */
    init(): Promise<Result<number>>;

    onCommandReceived(observer: CommandReceiveObserver): ClientCommandCommunication;

    onCommandResultReceived(observer: CommandResultReceiveObserver): ClientCommandCommunication;
}



import { CommandResultReceiveObserver } from './CommandResultReceiveObserver';


import { CommandReceiveObserver } from './CommandReceiveObserver';


import { Command } from './Command';


import { CommandResult } from './CommandResult';


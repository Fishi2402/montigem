/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { NotImplementedException } from './error/NotImplementedException';

/**
 * Simuliert die Kommunikation zwischen Server und einem Client
 * @class
 */
export interface ServerCommandCommunication {
    /**
     * Sendet ein Command zu einem Client
     * @param {Command} command
     */
    sendCommandToParticipant<T>(command: Command<T>);
}



import { Command } from './Command';


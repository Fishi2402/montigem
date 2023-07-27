/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Result } from '../common/Result';
import { CommandManager } from './CommandManager';

export class TestClientCommandManager extends CommandManager {
    commandCommunication: ClientCommandCommunication;

    public constructor(commandCommunication: ClientCommandCommunication) {
        super();
        if (this.commandCommunication === undefined) { this.commandCommunication = null; }
        this.commandCommunication = commandCommunication;
        commandCommunication.init().then((init: Result<number>) => {
          if (init.wasSuccessful()){
            this.participantId = init.getValue();
          } else {
          }
        });
    }

    /**
     *
     * @param {Command} command
     * @return {CommandResult}
     */
    async _executeCommand<T>(command: Command<T>): Promise<CommandResult<T>> {
        return await this.commandCommunication.sendCommandToServerAndReceiveResult<any>(command);
    }
}
TestClientCommandManager["__class"] = "umlp.backendrte.command.TestClientCommandManager";




import { Command } from './Command';


import { CommandResult } from './CommandResult';


import { ClientCommandCommunication } from './ClientCommandCommunication';


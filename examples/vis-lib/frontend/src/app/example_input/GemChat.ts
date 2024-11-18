import {GemChatComponent, config} from "@src/example_input/GemChatComponent";
import {Component} from "@angular/core";
import {Message} from "@src/domain/Message";
import {Role} from '@src/domain/Role';
import {DomainManager} from "@src/domain/DomainManager";
import {Answer} from "@src/domain/commands/Answer";
import {CommandManager, CommandResult} from "@umlp/common";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class GemChat extends GemChatComponent {

  public override sendMessage(msg: Message): (arg: any) => void {
    this.sendMessageHandler(msg);
    return null;

  }

  public async sendMessageHandler(msg: Message) {

    msg.setSender(Role.USER);
    msg.setReceiver(Role.SYSTEM);
    msg.setTimestamp(new Date());
    msg.build();
    this._userMsg = await DomainManager.messageBuilder();

    let answer = await CommandManager.executeCommand(new Answer(msg.getGemId()))
    console.log(answer)

    //CommandManager.executeCommand(new Answer(msg.getGemId())).then((answer: CommandResult<string>) => {
    //  console.log(answer);
    //})

    this.addMessages(answer.asSimpleResult().get());




  }


}

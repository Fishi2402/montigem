import {GemChatComponent, config} from "@src/example_input/GemChatComponent";
import {Component, OnInit} from "@angular/core";
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

  // Works in call, not in init
  sortMessages() {
    this.messages = this.messages.sort((a,b) => a.getTimestamp().getTime()-b.getTimestamp().getTime());
  }


  override ngOnInit() {
    this.init();
    this.sortMessages();
  }




  public async sendMessageHandler(msg: Message) {
    if (msg.getContent() == undefined) {
      // Handle Empty user input
      return;
    }

    // build new message
    msg.setSender(Role.USER);
    msg.setReceiver(Role.SYSTEM);
    msg.setTimestamp(new Date());
    msg.build();

    // reset UI message
    this._userMsg = await DomainManager.messageBuilder();


    // get answer from backend Service
    let answer = await CommandManager.executeCommand(new Answer(msg.getGemId()));


    //CommandManager.executeCommand(new Answer(msg.getGemId())).then((answer: CommandResult<string>) => {
    //  console.log(answer);
    //})

  //  this._messagesSetExternally = false;
  //  this.initMessages();
   // this.addMessages(answer.asSimpleResult().get());
    this.sortMessages();



  }


}

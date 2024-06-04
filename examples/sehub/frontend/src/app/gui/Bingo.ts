/* (c) https://github.com/MontiCore/monticore */
import {BingoComponent,config} from "../../../build/frontendGen/build/appgui/gui/BingoComponent";
import {Component} from '@angular/core';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {SehubManager} from "@src/sehub/SehubManager";
import {BoardMapper} from "@src/sehub/BoardMapper";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class Bingo extends BingoComponent {
  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/Login']);
    } else {
      this.router.navigate(['/Gui/Bingo/'+SehubSecurityManager.getCurrentUser().getGemId()])
    }

    super.ngOnInit();

  }

  public  checkButtonLeftClick (event: any,b: any): void
  {
    console.log(b)
    if (b.isIsChecked()) {
      this.addMessage( "UNCHECKED: " + this.limitString(b.getBingoBoardEntry().getWord()) )
      b.setIsChecked(false)
    }
    else {
      this.addMessage("CHECKED: " + this.limitString(b.getBingoBoardEntry().getWord()) )
      b.setIsChecked(true)
    }

    console.log(b.getGemId(), b.isIsChecked())
    b.build();
  }


  public  getCheckButtonLabel (b: BoardMapper): string
  {
    if (b.isIsChecked()) return "-- ✅ --"
    else return "Toggle"
  }

  public  chatLeftClick (event: any): void {

    let inputElement = document.getElementById('undefinedInput');

    if (inputElement instanceof HTMLInputElement) {
      let inputValue = inputElement.value;
      inputElement.value = "";
      console.log(inputValue);

      this.addMessage(inputValue);

    } else {
      console.warn('Element with ID "undefinedInput" is not an input or not found');
    }
  }

  public addMessage(message : string): void {
    let date = new Date();
    let messageText ="[" + this.user.getUsername() + "|" + date.getHours() + ":" + (date.getMinutes()<10?'0':'') + date.getMinutes() + "]: " + message;
    SehubManager.messageBuilder().then(b => {
      let message = b.text(messageText).build();
      this.messages.push(message);
      // Bad Workaround to reduce chats back to 40 if it managed to get somehow > 40

      if(this.messages.length > 31) {
        let m = this.messages[0];
        SehubManager.deleteMessage(m.getGemId());
        m = this.messages[0]
        SehubManager.deleteMessage(m.getGemId());
      } else if(this.messages.length > 30) {
        let m = this.messages[0]
        SehubManager.deleteMessage(m.getGemId());
      }
    });
  }

  public limitString(str, length = 50) {
    return str.length > length ? str.substring(0, length) + "..." : str;
  }

}

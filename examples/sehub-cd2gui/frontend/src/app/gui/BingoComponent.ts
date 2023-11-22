/* (c) https://github.com/MontiCore/monticore */
import {BingoComponentTOP} from "../../../build/frontendGen/build/appgui/gui/BingoComponentTOP";
import {Component,} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import { SehubManager } from "@src/sehub/SehubManager";
import { BoardMapper } from "@src/sehub/BoardMapper";


@Component({
  selector: 'bingo',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Bingo.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./Bingo.scss']
})

export class BingoComponent extends BingoComponentTOP {
  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/login/LoginPage']);
    } else {
      this.router.navigate(['/bingo/Bingo/'+SehubSecurityManager.getCurrentUser().getGemId()])
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

      if(this.messages.size() > 31) {
        this.messages.get(0).then(m => {
          SehubManager.deleteMessage(m.getGemId());
          this.messages.remove(0);
          this.messages.get(0).then(m => {
            SehubManager.deleteMessage(m.getGemId());
            this.messages.remove(0);
          })
        })

      } else if(this.messages.size() > 30) {
          this.messages.get(0).then(m => {
            SehubManager.deleteMessage(m.getGemId());
            this.messages.remove(0);
          })
        }
      })
  }

  public limitString(str, length = 50) {
    return str.length > length ? str.substring(0, length) + "..." : str;
  }

}

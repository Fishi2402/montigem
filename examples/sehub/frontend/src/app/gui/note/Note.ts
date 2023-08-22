

import { NoteTOP } from "@src/gui/note/NoteTOP";
import {Component} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import {PostBuilder} from "../../../../build/frontendGen/build/appdomain/sehub/PostBuilder";
import { Post } from '@src/sehub/Post';

@Component({
  selector: 'note',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/note/Note.html',
  styles: [":host{ display: flex; flex-direction: column }"],
})

export class Note extends NoteTOP {

  ngOnInit(): void {
    const tx = document.getElementsByTagName("gem-textarea");
    for (let i = 0; i < tx.length; i++) {
      tx[i].setAttribute("style", "height:" + (tx[i].scrollHeight) + "px;overflow-y:hidden;");
      tx[i].addEventListener("input", OnInput, false);
    }

    function OnInput() {
      this.style.height = 0;
      this.style.height = (this.scrollHeight) + "px";
    }
  }

  answerPost(event: any){
    SehubManager.postBuilder().then(builder => {
      let date = new Date();
      let response = builder.responses(
          new Set<Post>())
          .title(" " + this.post.getTitle() +  " (" + date.getHours() + ":" + date.getMinutes() + ")")
          .text("Eine Antwort ")
          .build();
      this.post.addResponses2(response)
    })
    console.log("Responding")
  }


  deletePost(event: any){
    SehubManager.deletePost(this.post.getGemId());
    console.log("Removing... " + this.post.getResponses())
  }



}

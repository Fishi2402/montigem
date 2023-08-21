

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

  answerPost(event: any){
    SehubManager.postBuilder().then(builder => {
      let response = builder.responses(
          new Set<Post>())
          .title("")
          .text("Eine Antwort " + new Date())
          .build();
      this.post.addResponses2(response)
      this.post._internal_getResponsesGemList().loadAll()
    })



    console.log("Responding")
  }


  deletePost(event: any){
    SehubManager.deletePost(this.post.getGemId()).then(() => {
      this.post._internal_getResponsesGemList().loadAll()
    })
    console.log("Removing... " + this.post.getResponses())
  }
}

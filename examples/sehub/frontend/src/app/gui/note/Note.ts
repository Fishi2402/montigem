

import { NoteTOP } from "@src/gui/note/NoteTOP";
import {Component} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import { Post } from '@src/sehub/Post';
import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";

@Component({
  selector: 'note',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/note/Note.html',
  styles: [":host{ display: flex; flex-direction: column }"],
})

export class Note extends NoteTOP {

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  ngOnInit(): void {
    console.log("Note Init")
    super.ngOnInit();

  }

  answerPost(event: any){
    SehubManager.postBuilder().then(builder => {
      let date = new Date();


      if(SehubSecurityManager.getCurrentUser() == undefined){
        alert("No User Logged In")
      } else {

        let response = builder.responses(
            new Set<Post>())
            .title(" " + this.post.getTitle() + " (" + date.getHours() + ":" + date.getMinutes() + ")")
            .author(SehubSecurityManager.getCurrentUser())
            .text("Eine Antwort ")
            .build();
        this.post.addResponses2(response)
      }
    })


    console.log("Responding")
  }


  deletePost(event: any){
    SehubManager.deletePost(this.post.getGemId());
    console.log("Removing... " + this.post.getResponses())
  }



}

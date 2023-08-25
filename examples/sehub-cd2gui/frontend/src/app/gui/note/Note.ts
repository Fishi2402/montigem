

import { NoteTOP } from "@src/gui/note/NoteTOP";
import {Directive} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import { Post } from '@src/sehub/Post';
import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {ViewChild} from '@angular/core';

@Directive()

export class Note extends NoteTOP {

  public showResponsesFlag: boolean

  public  getShowResponses (): boolean

  {
    return this.showResponsesFlag;
  }

  public  getReplybuttonLabel (): string

  {
    return "↪️ Reply (" + this.getPost().getResponses().length + ")";
  }

  answerPost(event: any){
    SehubManager.postBuilder().then(builder => {
      let date = new Date();


      if(SehubSecurityManager.getCurrentUser() == undefined){
        alert("No User Logged In")
      } else {

        let response = builder.responses(
            new Set<Post>())
            .title(" | " + date.getHours() + ":" + (date.getMinutes()<10?'0':'') + date.getMinutes() + " ]: " + this.post.getTitle())
            .author(SehubSecurityManager.getCurrentUser())
            .text("<< YOUR REPLY >>")
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

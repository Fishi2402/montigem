

import { NoteTOP } from "@src/gui/note/NoteTOP";
import {Component} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import { Post } from '@src/sehub/Post';
import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {ViewChild} from '@angular/core';

@Component({
  selector: 'note',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/note/Note.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./Note.scss']
})

export class Note extends NoteTOP {

  public showResponsesFlag: boolean

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  public  getShowResponses (): boolean

  {
    return this.showResponsesFlag;
  }

  public  getReplybuttonLabel (): string

  {
    return "↪️ Reply (" + this.getPost().getResponses().length + ")";
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

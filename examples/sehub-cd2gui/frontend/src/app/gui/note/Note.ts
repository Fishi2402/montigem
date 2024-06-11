/* (c) https://github.com/MontiCore/monticore */
import {NoteComponent,config} from "@src/gui/note/NoteComponent";
import {Component} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {Post} from '@src/sehub/Post';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {Gem} from '@umlp/common';
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class Note extends NoteComponent {

  public showResponsesFlag: boolean

  public  getShowResponses (): boolean {
    return this.showResponsesFlag;
  }

  public  getReplybuttonLabel (): string {
    return "↪️ Reply (" + this.getPost().getResponses().length + ")";
  }

  override answerPost(): (arg: void) => void {
    return $ => {
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
              .parentGem(new Gem<Post>().Gem2(this.getPost()))
              .build();
          this.post.addResponses2(response)
        }
      })
      console.log("Responding")
    }
  }

  override deletePost(): (arg: void) => void {
    return $ => {
      this.getPost().getIssueGem().isPresent().then(b => {
        if(b){
          this.getPost().getIssue().removePosts2(this.getPost())
        }
        SehubManager.deletePost(this.getPost().getGemId());
      })

      console.log("Removing... " + this.post.getResponses())
    }
  }

}

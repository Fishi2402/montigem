/* (c) https://github.com/MontiCore/monticore */
import {NoteComponent,config} from "gui/note/NoteComponent";
import {Component} from '@angular/core';
import {SehubManager} from 'sehub/SehubManager';
import {Post} from 'sehub/Post';
import {SehubSecurityManager} from "sehub/SehubSecurityManager";
import {Gem} from '@umlp/commonj2ts';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: "../../../../build/umlp_frontend/intermediate/appgui/gui/note/Note.html",
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class Note extends NoteComponent {

  public showResponsesFlag: boolean

  public  getShowResponses (): boolean {
    return this.showResponsesFlag;
  }

  public  getReplybuttonLabel (): string {
    return "↪️ Reply (" + this.getPost().getResponsesList().size() + ")";
  }

  override answerPost(): (arg: void) => void {
    return $ => {
      SehubManager.postBuilder().then(builder => {
        let date = new Date();

        if(!SehubSecurityManager.getCurrentUserIdOrNull()){
          alert("No User Logged In")
        } else {
          SehubSecurityManager.getCurrentUser().then(u => {
            builder.responses([])
                .then(builder => builder.title(" | " + date.getHours() + ":" + (date.getMinutes()<10?'0':'') + date.getMinutes() + " ]: " + this.post.getTitle()))
                .then(builder => builder.author(u))
                .then(builder => builder.text("<< YOUR REPLY >>"))
                .then(builder => builder.parent(this.getPost()))
                .then(builder => builder.build())
                .then(builder => this.post.addResponses$sehub_Post(builder))
          })
        }
      })
      console.log("Responding")
    }
  }

  override deletePost(): (arg: void) => void {
    return $ => {
      this.getPost().getIssueGem().isPresent().then(b => {
        if(b){
          this.getPost().getIssue().then(i => i.removePosts$sehub_Post(this.getPost()));
        }
        SehubManager.deletePost(this.getPost().getGemId());
      })

      console.log("Removing... " + this.post.getResponsesList())
    }
  }

}

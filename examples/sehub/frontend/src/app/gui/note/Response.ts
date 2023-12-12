/* (c) https://github.com/MontiCore/monticore */
import {SehubManager} from '@src/sehub/SehubManager';
import {Component} from '@angular/core';
import {ResponseComponent} from '@src/gui/note/ResponseComponent';
import {Post} from '@src/sehub/Post';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';
import {config} from "@src/gui/note/ResponseStandaloneConfig";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: 'response',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/note/Response.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./Note.scss']
})
export class Response extends ResponseComponent {

  deletePost(event: any){
    let parent: Post = this.getPost().getParent()
    parent.removeResponses2(this.getPost())
    SehubManager.deletePost(this.getPost().getGemId());
  }

  public override getGemButton8Disabled (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "OWNER") {
      return false;
    } else if (!!curUser && curUser.getUsername() === this.getPost().getAuthor().getUsername()) {
      return false;
    }
    return true;
  }

  public override getGemTextareaInput9Disabled (): boolean  {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "GUEST") {
      return true;
    }
    return false;
  }
}

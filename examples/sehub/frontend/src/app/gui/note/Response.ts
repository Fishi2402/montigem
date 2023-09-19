import {Directive} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {ResponseTOP} from '@src/gui/note/ResponseTOP';
import {Post} from '@src/sehub/Post';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';


@Directive()

export class Response extends ResponseTOP {

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

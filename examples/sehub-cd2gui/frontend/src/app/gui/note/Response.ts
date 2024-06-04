/* (c) https://github.com/MontiCore/monticore */
import {SehubManager} from '@src/sehub/SehubManager';
import {Component} from '@angular/core';
import {ResponseComponent,config} from '@src/gui/note/ResponseComponent';
import {Post} from '@src/sehub/Post';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
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
export class Response extends ResponseComponent {

  deletePost(): (arg: void) => void {
    return $ => {
      let parent: Post = this.getPost().getParent()
      parent.removeResponses2(this.getPost())
      SehubManager.deletePost(this.getPost().getGemId());
    }
  }
}

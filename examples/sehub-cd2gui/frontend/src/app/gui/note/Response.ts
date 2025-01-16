/* (c) https://github.com/MontiCore/monticore */
import {SehubManager} from 'sehub/SehubManager';
import {Component} from '@angular/core';
import {ResponseComponent,config} from 'gui/note/ResponseComponent';
import {Post} from 'sehub/Post';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {User} from 'sehub/User';
import {SehubRole} from 'sehub/SehubRole';
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

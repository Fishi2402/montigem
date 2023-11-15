/* (c) https://github.com/MontiCore/monticore */
import {Directive} from '@angular/core';
import {SehubManager} from '@src/sehub/SehubManager';
import {ResponseTOP} from '@src/gui/note/ResponseTOP';
import {Post} from '@src/sehub/Post';


@Directive()

export class Response extends ResponseTOP {

  deletePost(event: any){
    let parent: Post = this.getPost().getParent()
    parent.removeResponses2(this.getPost())
    SehubManager.deletePost(this.getPost().getGemId());
  }
}

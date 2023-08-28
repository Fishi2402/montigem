


import {Directive} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import {PostBuilder} from "../../../../build/frontendGen/build/appdomain/sehub/PostBuilder";
import { Post } from '@src/sehub/Post';
import { ResponseTOP } from '@src/gui/note/ResponseTOP';


@Directive()

export class Response extends ResponseTOP {

  deletePost(event: any){
    SehubManager.deletePost(this.post.getGemId())
    console.log("Removing Resonse")
  }
}

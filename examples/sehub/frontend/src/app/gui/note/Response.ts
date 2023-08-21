


import {Component} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import {PostBuilder} from "../../../../build/frontendGen/build/appdomain/sehub/PostBuilder";
import { Post } from '@src/sehub/Post';
import { ResponseTOP } from '@src/gui/note/ResponseTOP';


@Component({
  selector: 'response',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/note/Response.html',
  styles: [":host{ display: flex; flex-direction: column }"],
})

export class Response extends ResponseTOP {


  deletePost(event: any){
    SehubManager.deletePost(this.post.getGemId())
    console.log("Removing Resonse")
  }
}

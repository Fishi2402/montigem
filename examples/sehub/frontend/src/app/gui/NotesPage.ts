import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {
  Component,
} from '@angular/core';

import { SehubManager } from '@src/sehub/SehubManager';
import { Post } from '@src/sehub/Post';


@Component({
  selector: 'notes-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/NotesPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class NotesPage extends NotesPageTOP {

  newPost(event: any){
    SehubManager.postBuilder().then(builder => {
      let response = builder.responses(
          new Set<Post>())
          .title("")
          .text("Eine Antwort " + new Date())
          .build();
      this.issue.addPosts2(response)
    })
  }

}

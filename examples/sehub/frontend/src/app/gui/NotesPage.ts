import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {
  Component,
} from '@angular/core';
import {PostBuilder} from "../../../build/frontendGen/build/appdomain/sehub/PostBuilder";
import {UserBuilder} from "../../../build/frontendGen/build/appdomain/sehub/UserBuilder";
import {User} from "../../../build/frontendGen/build/appdomain/sehub/User";
import { SehubManager } from '@src/sehub/SehubManager';
import { Post } from '@src/sehub/Post';
import { GemList } from '@umlp/common';
import { PostGemList } from "@src/sehub/gemlists/PostGemList";
import { ActivatedRoute, Router } from '@angular/router';

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
      this.issue._internal_getPostsGemList().loadAll();
    })
  }

}

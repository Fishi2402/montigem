import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {Directive,} from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';
import {SehubManager} from "../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {Post} from "../../../build/frontendGen/build/appdomain/sehub/Post";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";


@Directive()

export class NotesPage extends NotesPageTOP {

  public  newPost (event: any): void {
    if (!SehubSecurityManager.isSignedIn()) {
      alert("No User Logged In")
    } else {
      SehubManager.postBuilder().then(builder => {
        let post = builder.responses(
            new Set<Post>())
            .author(SehubSecurityManager.getCurrentUser())
            .title("<< YOUR TITLE >>")
            .text("<< YOUR DESCRIPTION >> ")
            .build();
        this.issue.addPosts2(post)
      })
    }
  }

}

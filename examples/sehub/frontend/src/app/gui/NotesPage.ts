import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {
  Component,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import {SehubManager} from "../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {Post} from "../../../build/frontendGen/build/appdomain/sehub/Post";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";


@Component({
  selector: 'notes-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/NotesPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class NotesPage extends NotesPageTOP {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  ngOnInit() {
    super.ngOnInit();
    console.log("ISSUE: ", this.issue)
  }

  newPost(event: any){
    console.log("ISSUE: ", this.issue);

    if(SehubSecurityManager.getCurrentUser() == undefined){
      alert("No User Logged In")
    } else {

      SehubManager.postBuilder().then(builder => {
        let post = builder.responses(
            new Set<Post>())
            .author(SehubSecurityManager.getCurrentUser())
            .title("")
            .text("Eine Antwort " + new Date())
            .build();
        this.issue.addPosts2(post)
      })
    }
  }

}

import {NotesPageComponentTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageComponentTOP";
import {Component,} from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';
import {SehubManager} from "../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {Post} from "../../../build/frontendGen/build/appdomain/sehub/Post";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";


@Component({
  selector: 'notes-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/NotesPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./NotesPage.scss']
})

export class NotesPageComponent extends NotesPageComponentTOP {
  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  public  ngOnInit (): void {
    if (!SehubSecurityManager.isSignedIn()) {
      alert("No User Logged In")
      this.router.navigate(['/login']);
    }

    if(this.issueRoutingId > 0 && this.issue == null) {
      SehubManager.getIssueSure(this.issueRoutingId).then(gemPromiseRes => {
        this.issue = gemPromiseRes;

        this.issue._internal_getPostsGemList().build()

        });
    }

    this.init();
  }
}

/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import { SehubSecurityManager } from '@src/sehub/SehubSecurityManager';
import {ActivatedRoute, Router} from '@angular/router';
import {IssueTrackerPageComponentTOP} from '@src/gui/issues/IssueTrackerPageComponentTOP';
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  selector: 'issue-tracker-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/issues/IssueTrackerPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ["/Issue.scss"]
})

export class IssueTrackerPageComponent extends IssueTrackerPageComponentTOP {

  public ngOnInit(): void {
    if (!SehubSecurityManager.isSignedIn()) {
      this.router.navigate(['/Login']);
    }
    // super.ngOnInit();

    SehubManager.getIssueList().then(gemPromiseRes => {
      this.issues = gemPromiseRes;
      this.issues.toArray().forEach(i => i._internal_getPostsGemList().build())
    });

    this.init();
  }
}

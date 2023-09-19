import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {Directive,} from '@angular/core';
import {SehubManager} from "../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {Post} from "../../../build/frontendGen/build/appdomain/sehub/Post";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {Gem} from '@umlp/common';
import {Issue} from "@src/sehub/Issue";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';


@Directive()

export class NotesPage extends NotesPageTOP {

  newPost(event: any) {
    if (!SehubSecurityManager.isSignedIn()) {
      alert("No User Logged In")
    } else {
      SehubManager.postBuilder().then(builder => {
        let post = builder.responses(
            new Set<Post>())
            .author(SehubSecurityManager.getCurrentUser())
            .title("<< YOUR TITLE >>")
            .text("<< YOUR DESCRIPTION >> ")
            .issueGem(new Gem<Issue>().Gem2(this.getIssue()))
            .build();
        this.issue.addPosts2(post)
      })
    }
  }

  public override getGemButton5Disabled (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && SehubRole[curUser.getRole()] === "GUEST") {
      return true;
    }
    return false;
  }


  public override getIssueTitleDisabled (): boolean {
    return this.disableGuestReporterEdits();
  }

  public override getIssueDescriptionDisabled (): boolean {
    return this.disableGuestReporterEdits();
  }

  public disableGuestReporterEdits (): boolean {
    let curUser: User = SehubSecurityManager.getCurrentUser();
    if (!!curUser && (SehubRole[curUser.getRole()] === "GUEST" ||
                      SehubRole[curUser.getRole()] === "REPORTER")) {
      return true;
    }
    return false;
  }

}

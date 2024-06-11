/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {SehubManager} from "../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {Post} from "../../../build/frontendGen/build/appdomain/sehub/Post";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {Gem} from '@umlp/common';
import {Issue} from "@src/sehub/Issue";
import {User} from '@src/sehub/User';
import {SehubRole} from '@src/sehub/SehubRole';
import {ActivatedRoute, Router} from '@angular/router';
import {NotesPageComponent,config} from "../../../build/frontendGen/build/appgui/gui/NotesPageComponent";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class NotesPage extends NotesPageComponent {

  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      alert("No User Logged In")
      this._router.navigate(['gui/login/LoginPage']);
    }

    super.ngOnInit();
  }

  public override newPost(): (arg: void) => void {
    return $ => {
      if (!SehubSecurityManager.isSignedIn()) {
        alert("No User Logged In")
      } else {
        SehubManager.postBuilder().then(builder => {
          let post = builder.responses(
              new Set<Post>())
              .author(SehubSecurityManager.getCurrentUser())
              .title("")
              .text("")
              .issueGem(new Gem<Issue>().Gem2(this.getIssue()))
              .build();
          this.issue.addPosts2(post)
        })
      }
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

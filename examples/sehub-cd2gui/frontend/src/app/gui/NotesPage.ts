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

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  ngOnInit() {
    if (!SehubSecurityManager.isSignedIn()) {
      alert("No User Logged In")
      this.router.navigate(['gui/login/LoginPage']);
    }

    super.ngOnInit();
  }

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

}

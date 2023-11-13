/* (c) https://github.com/MontiCore/monticore */
import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {Directive,} from '@angular/core';
import {SehubManager} from "../../../build/frontendGen/build/appdomain/sehub/SehubManager";
import {Post} from "../../../build/frontendGen/build/appdomain/sehub/Post";
import {SehubSecurityManager} from "../sehub/SehubSecurityManager";
import {Gem} from '@umlp/common';
import {Issue} from "@src/sehub/Issue";


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

}

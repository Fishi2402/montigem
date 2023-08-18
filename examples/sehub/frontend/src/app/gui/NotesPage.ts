import {NotesPageTOP} from "../../../build/frontendGen/build/appgui/gui/NotesPageTOP";
import {
  Component,
} from '@angular/core';
import {PostBuilder} from "../../../build/frontendGen/build/appdomain/sehub/PostBuilder";
import {UserBuilder} from "../../../build/frontendGen/build/appdomain/sehub/UserBuilder";
import {User} from "../../../build/frontendGen/build/appdomain/sehub/User";
import { SehubManager } from '@src/sehub/SehubManager';

@Component({
  selector: 'notes-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/NotesPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class NotesPage extends NotesPageTOP {

  private postBuider : PostBuilder;


  ngOnInit() {
    this.posts.loadAll();
    this.posts.add(0, this.postBuider.text("Text - Test " + new Date()).build())

    super.ngOnInit();
  }

  newPost(event: any) {

    this.postBuider = new PostBuilder(2);

    console.log("Create Post");
    this.posts.loadAll()
    console.log(this.posts)

    this.postBuider.text("Text - Test " + new Date()).build()


  }
}

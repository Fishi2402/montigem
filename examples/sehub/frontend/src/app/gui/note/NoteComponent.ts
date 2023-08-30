
import { NoteComponentTOP } from "@src/gui/note/NoteComponentTOP";
import {Component} from '@angular/core';
import { SehubManager } from '@src/sehub/SehubManager';
import { Post } from '@src/sehub/Post';
import { ActivatedRoute, Router } from '@angular/router';
import {SehubSecurityManager} from "../../sehub/SehubSecurityManager";
import {ViewChild} from '@angular/core';

@Component({
  selector: 'note',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/note/Note.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./Note.scss']
})

export class NoteComponent extends NoteComponentTOP {

  ngOnInit(): void {

    super.ngOnInit();
    this.post._internal_getResponsesGemList().build();

  }
}

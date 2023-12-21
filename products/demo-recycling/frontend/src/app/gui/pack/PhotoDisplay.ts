/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { PhotoDisplayComponent } from '../../../../build/frontendGen/build/appgui/gui/pack/PhotoDisplayComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'photo-display',
  templateUrl: './PhotoDisplay.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class PhotoDisplay extends PhotoDisplayComponent {

  public getImagePath() : string {
    console.debug(this.getPack().getPhotoLocation()[0])
    if(this.getPack().getPhotoLocation()[0]) {
      console.debug(this.getPack().getPhotoLocation()[0])
      return "umlp/api/images/" + this.getPack().getPhotoLocation()[0];
    } else {
      return null
    }
  }
}



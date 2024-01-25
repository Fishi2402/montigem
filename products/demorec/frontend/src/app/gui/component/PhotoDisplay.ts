/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { PhotoDisplayComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/PhotoDisplayComponent';

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
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class PhotoDisplay extends PhotoDisplayComponent {

  public getImagePaths() : string[] {
    if(this.getComponent() && this.getComponent().getPhotoLocations()) {
      //console.debug(this.getComponent().getPhotoLocations())
      const photos: string[] = []
      this.getComponent().getPhotoLocations().forEach(photo => {
        photos.push("umlp/api/images/" + photo)
      })
      return photos
    }
    else {
      return null
    }
  }

  public getImagePath() : string {
    if(this.getComponent() && this.getComponent().getPhotoLocations()[0]) {
      //console.debug(this.getComponent().getPhotoLocations()[0])
      return "umlp/api/images/" + this.getComponent().getPhotoLocations()[0];
    }
    else {
      return null
    }
  }
}



/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from 'demorec/DemoRecManager';
import { BatteryPack } from 'demorec/BatteryPack';
import { Photo } from 'demorec/Photo';

import { GemButton } from "@umlp/basic";

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
  imports:[...config.imports, GemButton],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: [":host{ display: flex; flex-direction: row }"],
  styleUrls: config.styleUrls
})
export class PhotoDisplay extends PhotoDisplayComponent {

  public getPhotos() : Photo[] {
    if(this.getComponent()) {
      //console.debug(this.getComponent().getPhotoLocations())
      // Timeout so the Server has time to store the Picture
      //const photos: string[] = []
      return this.getComponent().getPhotoList()

          //.forEach(photo => {
        //photos.push("umlp/api/images/" + photo.getFilename())
      //})
    }
    else {
      return null
    }
  }

  public deletePhoto(photo: Photo) : void {
    this.getComponent().removePhoto2(photo)
    DemoRecManager.deletePhoto(photo.getGemId())
  }

  public getPhotoPath(photo : Photo) : string {
    return "umlp/api/images/"+photo.getFilename()
  }

  public openImageInNewTab(photo: Photo) {
    window.open(this.getPhotoPath(photo), '_blank')
  }

}

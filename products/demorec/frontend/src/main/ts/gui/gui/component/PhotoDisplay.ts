/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from 'demorec/DemoRecManager';
import { DemoRecComponent } from 'demorec/DemoRecComponent';
import { Photo } from 'demorec/Photo';

import { GemButton } from "@umlp/basic";

import { PhotoDisplayComponent, config } from 'gui/component/PhotoDisplayComponent';

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

  protected photoArray : Photo[] = [];

  public override setComponent(component : DemoRecComponent) {
    if(component) {
      component.getPhotoList().toArraySync().then(loadedArr => {
        this.photoArray = loadedArr;
      })
    }
    super.setComponent(component);
  }

  public async getPhotos() : Promise<Photo[]> {
    if(this.getComponent()) {
      //console.debug(this.getComponent().getPhotoLocations())
      // Timeout so the Server has time to store the Picture
      //const photos: string[] = []
      return await this.getComponent().getPhotoList().toArraySync()

          //.forEach(photo => {
        //photos.push("umlp/api/images/" + photo.getFilename())
      //})
    }
    else {
      return null
    }
  }

  public deletePhoto(photo: Photo) : void {
    this.getComponent().removePhoto(photo)
    DemoRecManager.deletePhoto(photo.getGemId())
  }

  public getPhotoPath(photo : Photo) : string {
    return "umlp/api/images/"+photo.getFilename()
  }

  public openImageInNewTab(photo: Photo) {
    window.open(this.getPhotoPath(photo), '_blank')
  }

}

/* (c) https://github.com/MontiCore/monticore */
import { DemoRecManager } from 'demorec/DemoRecManager';
import { DemoRecComponent } from 'demorec/DemoRecComponent';
import { Photo } from 'demorec/Photo';
import { LocalPhoto,} from 'demorec/LocalPhoto';
import { ExternalPhoto } from 'demorec/ExternalPhoto';

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
  templateUrl: "./PhotoDisplay.html",
  styles: [":host{ display: flex; flex-direction: row }"],
  styleUrls: config.styleUrls
})
export class PhotoDisplay extends PhotoDisplayComponent {

  protected photoArray : Photo[] = [];

  public override setComponent(component : DemoRecComponent) {
    if(component) {
      component.getPhotoList().toArrayDeepSync().then(loadedArr => {
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
      return await this.getComponent().getPhotoList().toArrayDeepSync()

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
    if(photo instanceof LocalPhoto) {
      DemoRecManager.deleteLocalPhoto(photo.getGemId());
    } else {
      DemoRecManager.deleteExternalPhoto(photo.getGemId());
    }
  }

  public getPhotoPath(photo : Photo) : string {
    if(photo) {
      return photo.getUrl();
    } else {
      return "";
    }
  }

  public openImageInNewTab(photo: Photo) {
    window.open(this.getPhotoPath(photo), '_blank')
  }

}

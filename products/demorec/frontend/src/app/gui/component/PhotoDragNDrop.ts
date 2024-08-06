/* (c) https://github.com/MontiCore/monticore */

import { DemoRecComponent } from '@src/demorec/DemoRecComponent';
import { DemoRecManager } from '@src/demorec/DemoRecManager';
import { Photo } from '@src/demorec/Photo';

import { PhotoDragNDropComponent, config } from '../../../../build/frontendGen/build/appgui/gui/component/PhotoDragNDropComponent';

import { HttpClient, HttpHeaders } from '@angular/common/http';

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
  styleUrls: ["./PhotoDragNDrop.scss"]
})
export class PhotoDragNDrop extends PhotoDragNDropComponent {

  constructor(private http: HttpClient) {
    super();
  }


  @Output()
  public createPhoto : EventEmitter<Photo> = new EventEmitter<Photo>();

  public stateUploading : boolean = false;

  public files: File[] = [];
  uploadFailureMessage : boolean = false;
  errorMessage : string = "";

  onFileDropped(event: any): void {
    event.preventDefault()
    const files = event.dataTransfer.files;
    this.addFiles(files);
  }

  onFileSelected(event: any): void {
    const files = event.target.files;
    this.addFiles(files);
  }

  private addFiles(files: FileList): void {
    for (let i = 0; i < files.length; i++) {
      this.files.push(files[i]);
    }
  }

  preventDefault(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  public uploadFile(file: File, name :string ): void {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    const headers = new HttpHeaders({
      'name': name,
    });
    console.debug("Trying to sending image "+ file.name+ " to server.")
    this.http.post(`/umlp/api/blob`, formData, { headers }).subscribe(
    (response) => {
      DemoRecManager.getPhotoSure(response['gemId']).then((photo : Photo) => {
        console.debug("Retrieved Photo Created in Backend with GemId: ", response)
        this.createPhoto.emit(photo)
      })
      console.warn(this.files)
      const index = this.files.indexOf(file)
      console.warn(index)
      this.files.splice(index, 1)
      console.warn(this.files)
      if(this.files.length === 0) {
        this.stateUploading = false;
      }
    },
    (error) => {
      this.uploadFailureMessage = true
      this.errorMessage = error.error
    });
  }

  public override uploadAllFiles(): (arg: void) => void  {
    return $ =>  {
      this.uploadFailureMessage = false;
      this.stateUploading = true;
      const files = Array.from(this.files)
      files.forEach(f =>
          {
            const name = this.getAttachedComponent().replace(" ","_")
            this.uploadFile(f, name)
          }
      )
    }
  }

  public getUploadDisabled() : boolean {
    if(this.files.length >= 1) {
      return false;
    }
    return true;
  }

  public override cancelUpload(): (arg: void) => void {
    return $ => this.files = [];
  }

}

/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { PhotoDragNDropComponent } from '../../../../build/frontendGen/build/appgui/gui/pack/PhotoDragNDropComponent';

import { HttpClient, HttpHeaders } from '@angular/common/http';

import { BatteryPackBuilder } from '@src/demorec/BatteryPackBuilder';

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
  selector: 'photo-drag-n-drop',
  templateUrl: './PhotoDragNDrop.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export class PhotoDragNDrop extends PhotoDragNDropComponent {

  constructor(private http: HttpClient,route: ActivatedRoute, router: Router) {
    super(route, router)
  }

  @Input()
  public attachedPack: BatteryPackBuilder;

  files: File[] = [];

  onFileDropped(event: any): void {
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

  //private baseUrl = 'http://localhost:8080'; // Replace with your server URL

  public uploadFile(file: File): void{
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    const headers = new HttpHeaders({
      'PATH': "",
    });

    this.http.post(`/umlp/api/blob`, formData, { headers }).subscribe();
  }

  public override uploadAllFiles (event: any): void {
    let size = this.getAttachedPack().getPhotoLocation().length
    this.files.forEach(f =>
      {
        this.uploadFile(f)
        this.getAttachedPack().addPhotoLocation(size, f.name)
        size++;
      }
    )
  }

  public  getAttachedPack (): BatteryPackBuilder {
    return this.attachedPack;
  }
}



/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';
import {config} from "@src/gui/widgets/GitLabIconComponent";
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class GitLabIcon {

  private _source: SafeHtml | string = "";

  @Input()
  public status: string = 'none';

  @Input()
  public set source(value: SafeHtml | string) {
    if (typeof value === 'string') {
      this.http.get(value, { responseType: 'text' })
        .subscribe(data => this._source = this.sanitizer.bypassSecurityTrustHtml(data));
    } else {
      this._source = value;
    }
  };

  public get source(): SafeHtml | string {
    return this._source;
  };

  public constructor(protected sanitizer: DomSanitizer, protected http: HttpClient) {
  }

}



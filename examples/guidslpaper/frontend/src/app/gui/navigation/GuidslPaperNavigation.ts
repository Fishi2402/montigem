/* (c) https://github.com/MontiCore/monticore */

import {Person} from 'guidslpaper/Person';
import {GuidslPaperNavigationComponent,config} from 'gui/navigation/GuidslPaperNavigationComponent';
import {Component} from '@angular/core';
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';

@Component({
  standalone:true,
  imports:[... config.imports,RouterModule,CommonModule],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})

export class GuidslPaperNavigation extends GuidslPaperNavigationComponent {

  public getPersonNavTarget(p: Person): string {
    return "/gui/personcreate/" + p.getGemId();
  }

}



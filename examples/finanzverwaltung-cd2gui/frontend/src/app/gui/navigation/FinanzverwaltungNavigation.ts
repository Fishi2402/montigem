/* (c) https://github.com/MontiCore/monticore */

import {FinanzverwaltungNavigationComponent,config} from 'gui/navigation/FinanzverwaltungNavigationComponent';
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

export class FinanzverwaltungNavigation extends FinanzverwaltungNavigationComponent {


}



/* (c) https://github.com/MontiCore/monticore */

import { EndEffector } from 'demorec/EndEffector';
import { EndEffectorOverviewComponent, config } from '../../../../build/frontendGen/build/appgui/gui/endeffector/EndEffectorOverviewComponent';

import {
  Component,
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
export class EndEffectorOverview extends EndEffectorOverviewComponent {

/*  override init(): void {
    this.endEffectorsTableColumns = [];
    this.endEffectorsTableColumns.push(new TableColumn("_name", "Name"));
  }

  public override createEndEffector (event: any): void {

  }*/

}



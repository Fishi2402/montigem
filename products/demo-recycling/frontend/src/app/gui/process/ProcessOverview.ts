/* (c) https://github.com/MontiCore/monticore */


import { BatteryPackBuilder } from '@src/demorec/BatteryPackBuilder';

import { ProcessBuilder } from '@src/demorec/ProcessBuilder';

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackTypeBuilder } from '@src/demorec/BatteryPackTypeBuilder';

import { BatteryPack } from '@src/demorec/BatteryPack';

import { ProcessOverviewComponent } from '@src/gui/process/ProcessOverviewComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

const newOption = "-- Create new --";

@Component({
  selector: 'process-overview',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/process/ProcessOverview.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ProcessOverview extends ProcessOverviewComponent {


}



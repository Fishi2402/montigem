/* (c) https://github.com/MontiCore/monticore */

import { DemoRecManager } from '@src/demorec/DemoRecManager';

import { BatteryPackType } from '@src/demorec/BatteryPackType';

import { BatteryPack } from '@src/demorec/BatteryPack';
import { JoinConnection } from '@src/demorec/JoinConnection';

import { ComponentTreeViewComponent } from '../../../../build/frontendGen/build/appgui/gui/component/ComponentTreeViewComponent';

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
  selector: 'component-tree-view',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/component/ComponentTreeView.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})
export  class ComponentTreeView extends ComponentTreeViewComponent {


}



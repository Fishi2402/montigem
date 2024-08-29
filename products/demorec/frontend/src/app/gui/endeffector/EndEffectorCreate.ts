/* (c) https://github.com/MontiCore/monticore */

import {DemoRecManager} from '@src/demorec/DemoRecManager';

import {
  EndEffectorCreateComponent,
  config
} from '@src/gui/endeffector/EndEffectorCreateComponent';

import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import {ActivatedRoute, Router} from '@angular/router';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class EndEffectorCreate extends EndEffectorCreateComponent {

  public override createEndEffector(): (arg: void) => void {
    return $ => {
      this.getEndEffector().build();
      DemoRecManager.endEffectorBuilder().then(tmp_$ => {
        this._endEffector = tmp_$
      });
    }
  }
}



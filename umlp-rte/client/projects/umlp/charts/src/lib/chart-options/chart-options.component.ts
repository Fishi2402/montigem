import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'chart-options',
  templateUrl: './chart-options.component.html',
  styleUrls: ['./chart-options.component.scss']
})

export class ChartOptionsComponent {

  public buttonClicked: boolean = false;

}

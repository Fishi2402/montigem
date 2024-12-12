/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from '@angular/core';
import { config, GemChartLegendComponent } from './build/gui/mc/fenix/charts/GemChartLegendComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemChartLegend extends GemChartLegendComponent {
  /**contains the entries of the legend */
  _entries: { label: string, color: string }[] = [];

  /** setter that processes the color data to be shown correctly by the legend. */
  @Input()
  set colors(colors: string[]) {
    this._entries = [];

    for (let i = 0; i < this._labels.length; i++) {
      let legendEntry = {
        label: this._labels[i],
        color: colors[i]
      };
      this._entries.push(legendEntry);
    }
  }
}
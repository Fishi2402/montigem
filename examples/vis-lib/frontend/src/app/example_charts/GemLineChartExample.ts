/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { GemLineChartExampleComponent } from '@src/example_charts/GemLineChartExampleComponent';
import { GemLineChartData, GemLineChartEntry } from '@umlp/charts';

@Component({
  selector: 'gem-line-chart-example',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemLineChartExample.html'
})
export class GemLineChartExample extends GemLineChartExampleComponent {

  labels: string[] = ['January', 'February', 'March', 'April', 'May', 'June'];

  entries: GemLineChartEntry[] = [
    { data: [132.54, 136.96, 153.65, 148.96, 182.01, 164.85], label: 'Apple Inc' },
    { data: [252.18, 271.60, 301.88, 329.37, 336.32, 294.95], label: 'Microsoft Corp' },
    { data: [138.82, 134.14, 139.24, 140.28, 152.68, 120.46], label: 'Siemens AG' },
    { data: [252.12, 268.63, 238.38, 300.95, 237.92, 151.25], label: 'Spotify Tech. SA' },
  ];

  data: GemLineChartData = {
    entries: this.entries,
    labels: this.labels
  }
}

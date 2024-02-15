/* (c) https://github.com/MontiCore/monticore */

import { Component } from "@angular/core";
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import {CommonModule} from '@angular/common';
@Component({
  standalone:true,
  imports:[RouterModule,CommonModule],
  selector: "navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"]
})
export class Navigation {

  public constructor(protected router: Router) {
    this.router = router;
  }

  readonly items: { url: string, title: string }[] = [
    { url: '/person/Overview', title: 'Overview' },
    { url: '/example_basic/GemAudioExample', title: 'Audio' },
    { url: '/example_basic/GemButtonExample', title: 'Button' },
    { url: '/example_basic/GemImageExample', title: 'Image' },
    { url: '/example_basic/GemLinkExample', title: 'Link' },
    { url: '/example_basic/GemTextExample', title: 'Text' },
    { url: '/example_basic/GemTreeviewExample', title: 'Treeview' },
    { url: '/example_basic/GemVideoExample', title: 'Video' },
    { url: '/example_table/GemTableExample', title: 'Table' },
    { url: '/example_arrange/GemCardExample', title: 'Card' },
    { url: '/example_arrange/GemColumnExample', title: 'Column' },
    { url: '/example_arrange/GemColumnExample2', title: 'Column2' },
    { url: '/example_arrange/GemRowExample', title: 'Row' },
    { url: '/example_arrange/GemRowExample2', title: 'Row2' },
    { url: '/example_input/GemTextInputExample', title: 'TextInput' },
    { url: '/example_input/GemTextareaInputExample', title: 'TextareaInput' },
    /*
    { url: '/example_input/GemRangeInputExample', title: 'RangeInput' },
    { url: '/example_input/GemCheckBoxInputExample', title: 'CheckBoxInput' },
    { url: '/example_input/GemRadioInputExample', title: 'RadioInput' },
    */
    { url: '/example_charts/GemBarChartExample', title: 'BarChart' },
    { url: '/example_charts/GemBulletChartExample', title: 'BulletChart' },
    { url: '/example_charts/GemCandlestickChartExample', title: 'CandlestickChart' },
    { url: '/example_charts/GemGaugeChartExample', title: 'GaugeChart' },
    { url: '/example_charts/GemLineChartExample', title: 'LineChart' },
    { url: '/example_charts/GemHeatmapChartExample', title: 'HeatmapChart' },
    { url: '/example_charts/GemPieChartExample', title: 'PieChart' },
    { url: '/example_charts/GemSunburstChartExample', title: 'SunburstChart' },
    { url: '/example_charts/GemVennDiagramExample', title: 'VennDiagram' },
    { url: '/example_charts/GemGraphChartExample', title: 'NetworkChart' },
    { url: '/example_network/NetworkChartExample/', title: 'NewNetworkChart'}
  ];

}

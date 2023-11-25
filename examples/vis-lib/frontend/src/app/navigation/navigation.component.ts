import { Component } from "@angular/core";
import { Router } from '@angular/router';

@Component({
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
    //{ url: '/example_input/GemRangeInputExample', title: 'RangeInput' },
    //{ url: '/example_table/GemTableExample', title: 'Table' },
    //{ url: '/example_arrange/GemCardExample', title: 'Card' },
    //{ url: '/example_arrange/GemColumnExample', title: 'Column' },
    //{ url: '/example_arrange/GemRowExample', title: 'Row' },
    //{ url: '/example_input/GemTextInputExample', title: 'TextInput' },
    { url: '/example_input/GemTextareaInputExample', title: 'TextareaInput' },
    { url: '/example_input/GemCheckBoxInputExample', title: 'CheckBoxInput' },
    { url: '/example_input/GemRadioInputExample', title: 'RadioInput' },
    { url: '/example_basic/GemTreeviewExample', title: 'Treeview' },
    { url: '/example_basic/GemVideoExample', title: 'Video' },
    { url: '/example_charts/GemBarChartExample', title: 'BarChart' },
    { url: '/example_charts/GemBulletChartExample', title: 'BulletChart' },
    { url: '/example_charts/GemCandlestickChartExample', title: 'CandlestickChart' },
    { url: '/example_charts/GemGaugeChartExample', title: 'GaugeChart' }



  ];

}

import { GemVennDiagramExampleComponentTOP } from '@src/example_charts/GemVennDiagramExampleComponentTOP';
import { Component } from '@angular/core';
import { GemVennDiagramData } from '@umlp/charts';
import { GemVennDiagramEntry } from '@umlp/charts';

@Component({
  selector: 'GemVennChartExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_charts/GemVennDiagramExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemVennDiagramExampleComponent extends GemVennDiagramExampleComponentTOP  {


 entries: GemVennDiagramEntry[] = [
  {
    label: 'Test 1',
    sets: ['test1'],
    size: 6
  },
  {
    label: 'Test 2',
    sets: ['test2'],
    size: 3
  },
  {
    label: 'Test 3',
    sets: ['test3'],
    size: 2
  },
  {
    sets: ['test1', 'test2'],
    size: 1
  }
];
 data: GemVennDiagramData = {
  entries:this.entries,
}
}

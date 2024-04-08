import { ChartsModule } from "ng2-charts";
import { CommonModule } from "@angular/common";
import { GemScatterPlot } from "./gem-scatter-plot.component";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { GemChartLegend } from "../gemchartlegend/gem-chart-legend.component";

describe("NewScatterPlotComponent", () => {
  let component: GemScatterPlot;
  let fixture: ComponentFixture<GemScatterPlot>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [GemScatterPlot, GemChartLegend],
      imports: [CommonModule, ChartsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GemScatterPlot);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  //TODO test the component I wrote

//   it("should set properly the property options when stacked is entered", () => {
//     component.stacked = true;
//     expect(component.options.scales.xAxes[0].stacked).toBe(true);
//     expect(component.options.scales.yAxes[0].stacked).toBe(true);
//   });
//
//   it("should set properly the grid lines of the chart based on its type", () => {
//     expect(component.options.scales.xAxes[0].gridLines.display).toBe(false);
//     expect(component.options.scales.yAxes[0].gridLines.display).toBe(false);
//   });
//
//   it("should set properly max and min of the chart based on its type", () => {
//     component.maxValue = 10;
//     expect(component.options.scales.yAxes[0].ticks.max).toBe(10);
//     component.minValue = 5;
//     expect(component.options.scales.yAxes[0].ticks.min).toBe(5);
//     expect(component.options.scales.yAxes[0].ticks.min).toBe(undefined);
//     expect(component.options.scales.yAxes[0].ticks.max).toBe(undefined);
//     expect(component.options.scales.xAxes[0].ticks.min).toBe(5);
//     expect(component.options.scales.xAxes[0].ticks.max).toBe(10);
//   });
//
//   it("should add a color to an entry without entered backgroundColor", () => {
//     component.data = {
//       entries: [
//         {
//           data: [12, 25, 13, 25],
//           label: "Title 1",
//         },
//       ],
//       labels: ["January"],
//     };
//     expect(component.dataSets[0].backgroundColor).not.toBeUndefined;
//   });
//
//   it("should set the property stack of the entries only when stacked equals true ", () => {
//     component.data = {
//       entries: [
//         {
//           data: [12, 25, 13, 25],
//           label: "Title 1",
//           stack: "Group1",
//         },
//       ],
//       labels: ["January"],
//     };
//     expect(component.dataSets[0].stack).toBeUndefined;
//     component.stacked = true;
//     expect(component.dataSets[0].stack).toBe("Group1");
//   });
});

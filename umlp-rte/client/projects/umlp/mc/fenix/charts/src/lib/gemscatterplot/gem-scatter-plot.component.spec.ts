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

  it("should properly set the axis labels", () => {
    component.xAxis = {
      label: "x",
    };
    component.yAxis = {
      label: "y",
    };
    expect(component.options.scales.x.title.display).toBe(true);
    expect(component.options.scales.x.title.text).toBe("x");
    expect(component.options.scales.y.title.display).toBe(true);
    expect(component.options.scales.y.title.text).toBe("y");
  });

  it("should properly set the axis step width", () => {
    component.xAxis = {
      stepWidth: 2,
    };
    expect(component.options.scales.x.stepSize).toBe(2);
    component.yAxis = {
      stepWidth: 3,
    };
    expect(component.options.scales.y.stepSize).toBe(3);
  });

  it("should properly set min and max value of the axes", () => {
    component.xAxis = {
      minValue: 0,
      maxValue: 2,
    };
    component.yAxis = {
      minValue: 1,
      maxValue: 3,
    };
    expect(component.options.scales.x.min).toBe(0);
    expect(component.options.scales.x.max).toBe(2);
    expect(component.options.scales.y.min).toBe(1);
    expect(component.options.scales.y.max).toBe(3);
  });

  it("should properly set the shape property", () => {
    component.data = {
      sets: [{
        label: "foo",
        points: [],
        shape: GemScatterPlotShape.ROTATED_CROSS,
      }],
    };
    expect(component.dataSets[0].pointStyle).toBe("crossRot");
  });

  it("should copy over data points", () => {
    component.data = {
      sets: [{
        label: "foo",
        points: [{
          x: 3,
          y: 7,
        }],
      }],
    };
    expect(component.dataSets[0].data).toBe([{
      x: 3,
      y: 7,
    }]);
  });

  it("should properly set the data label", () => {
    component.data = {
      sets: [{
        label: "foo",
        points: [],
      }],
    };
    expect(component.dataSets[0].label).toBe("foo");
  });

  it("should validate colors", () => {
    component.data = {
      sets: [{
        label: "foo",
        points: [],
        color: "fizz buzz",
      }],
    };
    expect(component.dataSets[0].color).not.toBe("fizz buzz");
    expect(component.dataSets[0].color).not.toBeUndefined;
    expect(component.dataSets[0].backgroundColor).not.toBeUndefined;
  });
});

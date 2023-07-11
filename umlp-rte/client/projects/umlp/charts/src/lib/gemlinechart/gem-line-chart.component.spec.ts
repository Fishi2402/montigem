import { ChartsModule } from "ng2-charts";
import { CommonModule } from "@angular/common";
import { GemLineChart } from "./gem-line-chart.component";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { GemChartLegend } from '../gemchartlegend/gem-chart-legend.component';

describe("NewBarChartComponent", () => {
  let component: GemLineChart;
  let fixture: ComponentFixture<GemLineChart>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [GemLineChart, GemChartLegend],
      imports: [CommonModule, ChartsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GemLineChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it('should add the whole needed set of colors to an entry, in which is not entered such property', () => {
    component.data = {
      entries: [{ data: [85], label: 'Benzin', }],
      labels: ["January"]
    };
    expect(component.colors[0].borderColor).not.toBeUndefined;
    expect(component.colors[0].backgroundColor).not.toBeUndefined;
    expect(component.colors[0].pointBackgroundColor).not.toBeUndefined;
  });

  it('should set properly the min and max when specific properties are entered for them', () => {
    component.minValue = 10;
    expect(component.options.scales.yAxes[0].ticks.min).toBe(10);
    component.maxValue = 100;
    expect(component.options.scales.yAxes[0].ticks.max).toBe(100);
  });

  it('should set properly options when enablebackgroundColor is entered', () => {
    expect(component.options.elements.line.fill).toBe(false);
    component.enableBackgroundColor = true;
    expect(component.options.elements.line.fill).toBe(true);
  })
});
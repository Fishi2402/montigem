import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NgChartsModule } from "ng2-charts";
import { GemPieChart } from "./gem-pie-chart.component";
import { GemChartLegend } from "../gemchartlegend/gem-chart-legend.component";

describe("NewPieChartComponent", () => {
  let component: GemPieChart;
  let fixture: ComponentFixture<GemPieChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgChartsModule],
      declarations: [GemPieChart, GemChartLegend],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GemPieChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should check whether the function coloursWithoutInput works", () => {
    component.data = {
      entries: [
        {
          value: 100,
          label: "Title 1",
        },
      ],
    };
    expect(component.colors[0].backgroundColor).not.toBeUndefined;
  });
});

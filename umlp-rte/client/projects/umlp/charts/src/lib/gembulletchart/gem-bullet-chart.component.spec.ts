import { ComponentFixture, TestBed } from '@angular/core/testing';
import * as PlotlyJS from 'plotly.js-dist-min';
import { PlotlyModule } from 'angular-plotly.js';
// We have to supply the plotly.js module to the Angular
// library.
PlotlyModule.plotlyjs = PlotlyJS;
import { GemBulletChart } from './gem-bullet-chart.component';

describe('BulletChartComponent', () => {
  let component: GemBulletChart;
  let fixture: ComponentFixture<GemBulletChart>;

  const setvalue = (newvalue) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        component.value = newvalue;
        resolve(component._data[0].value);
      }, 1000);
    })
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GemBulletChart],
      imports: [PlotlyModule]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GemBulletChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change threshold line color to green', () => {
    component.value = 270;
    component.thresholdvalue = 250;
    expect(component._data[0].gauge.threshold.line.color).toBe("#11750e");
  });

  it('should change threshold line color to red', () => {
    component.value = 250;
    component.thresholdvalue = 270;
    expect(component._data[0].gauge.threshold.line.color).toBe("#cc071e");
  });

  it('should change value from 250 to 260', async () => {
    component.value = 250;
    expect(component._data[0].value).toBe(250);
    await setvalue(260);
    expect(component._data[0].value).toBe(260);
  });

  it('should adapt left margin to fit title', () => {
    component.title = "Profit";
    const currentmargin = component._layout.margin.l;
    component.title = "Total Profit";
    expect(component._layout.margin.l).toBeGreaterThan(currentmargin);
  });

  it('should not adapt left margin', () => {
    component.title = "Profit";
    const currentmargin = component._layout.margin.l;
    component.title = "tiforP";
    expect(component._layout.margin.l).toBe(currentmargin);
  });
});

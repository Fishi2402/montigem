import { ComponentFixture, TestBed } from '@angular/core/testing';
import * as PlotlyJS from 'plotly.js-dist-min';
import { PlotlyModule } from 'angular-plotly.js';
// We have to supply the plotly.js module to the Angular
// library.
PlotlyModule.plotlyjs = PlotlyJS;
import { GemCandlestickChart } from './gem-candlestick-chart.component';

describe('CandlestickChartComponent', () => {
  let component: GemCandlestickChart;
  let fixture: ComponentFixture<GemCandlestickChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GemCandlestickChart],
      imports: [PlotlyModule]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GemCandlestickChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show buttons', () => {
    component.builtinbuttons = true;
    component.layout([], "");
    expect(component._layout.xaxis.rangeselector.buttons).not.toEqual([]);
  });

  it('button "1 week" should properly adjust range', async () => {
    component.data = [
      { label: '2017-01-17', value: [118, 144, 117, 136] },
      { label: '2017-01-18', value: [133, 142, 128, 140] },
      { label: '2017-01-19', value: [138, 140, 118, 123] },
      { label: '2017-01-20', value: [120, 143, 109, 131] }
    ];
    component.updatelayout(1);
    expect(component._layout.xaxis.range).toEqual(['2017-01-13 12:00', '2017-01-20 12:00']);
  });

  it('button "2 weeks" should properly adjust range', async () => {
    component.data = [
      { label: '2017-01-17', value: [118, 144, 117, 136] },
      { label: '2017-01-18', value: [133, 142, 128, 140] },
      { label: '2017-01-19', value: [138, 140, 118, 123] },
      { label: '2017-01-20', value: [120, 143, 109, 131] }
    ];
    component.updatelayout(5);
    expect(component._layout.xaxis.range).toEqual(['2017-01-06 12:00', '2017-01-20 12:00']);
  });

  it('should change chart type', () => {
    expect(component._data[0].type).toBe("candlestick");
    for (let index = 0; index < 10; index++) {
      component.updatelayout(4);
      expect(component._data[0].type).toBe("ohlc");
      component.updatelayout(4);
      expect(component._data[0].type).toBe("candlestick");
    }
  });
});

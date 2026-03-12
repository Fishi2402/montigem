/* (c) https://github.com/MontiCore/monticore */
import {Component, ElementRef, ViewChild, AfterViewInit, OnDestroy, HostBinding, Input} from '@angular/core';
import {GemTimeSeriesChartComponent, config} from '../../../../../../build/gui2java2ts/mc/fenix/charts/GemTimeSeriesChartComponent';
import ApexCharts from 'apexcharts';
import { TimeSeriesData } from '../../../../../../build/gui2java2ts/mc/fenix/charts/gemtimeseriescharttypes/TimeSeriesData';
import { TimeSeries } from '../../../../../../build/gui2java2ts/mc/fenix/charts/gemtimeseriescharttypes/TimeSeries';
import { TimeSeriesDataPoint } from '../../../../../../build/gui2java2ts/mc/fenix/charts/gemtimeseriescharttypes/TimeSeriesDataPoint';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: [...config.styleUrls]
})
export class GemTimeSeriesChart extends GemTimeSeriesChartComponent implements OnDestroy, AfterViewInit {

  @HostBinding('style.height') get styleHeight() { return this.getHeight(); }
  @HostBinding('style.width') get styleWidth() { return this.getWidth(); }

  options: any;
  chart: ApexCharts;
  @ViewChild('tSChart') targetElement! : ElementRef<HTMLDivElement>;
  private liveData: boolean = true;
  private resizeObserver: ResizeObserver;

  // Data Range (for external control)
  public dataMinTime: number = 0;
  public dataMaxTime: number = 0;

  constructor(private hostElement: ElementRef) {
    super();
  }

  public async init() {
    await super.init();

    this.options = {
      animations: { enabled: false },
      noData: { text: "Loading...", align: 'center', verticalAlign: 'middle', style: { color: "#000000", fontSize: '14px' } },
      chart: {
        id: 'area-datetime',
        type: 'area',
        height: '100%',
        width: '100%',
        zoom: { autoScaleYaxis: true },
        events: {
          legendClick: function(chartContext, seriesIndex, opts) {
            this.options.series[seriesIndex].hidden = !this.options.series[seriesIndex].hidden;
          }.bind(this)
        }
      },
      series: [],
      xaxis: {
        type: 'datetime',
        tickAmount: 6,
        labels: {
          datetimeFormatter: {
            year: 'yyyy',
            month: 'MMM \'yy',
            day: 'dd MMM',
            hour: 'HH:mm'
          }
        }
      },
      dataLabels: { enabled: false },
      markers: { size: 0, style: 'hollow' },
      fill: {
        type: 'gradient',
        gradient: {
          shadeIntensity: 1,
          opacityFrom: 0.7,
          opacityTo: 0.9,
          stops: [0, 100]
        }
      },
    };

    if (this.targetElement) {
      this.chart = new ApexCharts(this.targetElement.nativeElement, this.options);
      this.chart.render();
    }
  }

  ngAfterViewInit() {
    this.resizeObserver = new ResizeObserver(() => {
      if (this.chart) {
        this.chart.updateOptions({ chart: { height: '100%', width: '100%' } }, false, false);
      }
    });
    this.resizeObserver.observe(this.hostElement.nativeElement);
  }

  ngOnDestroy() {
    if (this.resizeObserver) this.resizeObserver.disconnect();
    if (this.chart) this.chart.destroy();
  }

  public setData(val: TimeSeriesData) {
    super.setData(val);
    this.processNewData(val);
  }

  id2TimeSeriesIndex: Map<number, number> = new Map();

  private async processNewData(data: TimeSeriesData): Promise<void> {
    let min = Number.MAX_VALUE;
    let max = Number.MIN_VALUE;
    let hasData = false;

    for await (const series of data.getSeriesList()) {
      if (this.id2TimeSeriesIndex.has(series.getIdentifier())) {
        this.options.series[this.id2TimeSeriesIndex.get(series.getIdentifier())] = await this.transformData2Series(series);
      } else {
        this.id2TimeSeriesIndex.set(series.getIdentifier(), this.options.series.length);
        this.options.series.push(await this.transformData2Series(series));
      }

      for await (const dp of series.getDatapointsList()) {
          const t = dp.getTimestamp().getTime();
          if (t < min) min = t;
          if (t > max) max = t;
          hasData = true;
      }
    }

    if (hasData) {
        this.dataMinTime = min;
        this.dataMaxTime = max;
    }

    if (this.liveData) {
      this.chart.updateSeries(this.options.series, false);
    }
    
    let events = await this.transformEvent2Annotation(data);
    this.chart.clearAnnotations();
    for await (const event of events) {
      this.chart.addXaxisAnnotation(event);
    }
  }

  private async transformData2Series(data: TimeSeries): Promise<{name: string, data: any[], color?: string, hidden?: boolean}> {
    let seriesData = [];
    for await (const dataPoint of data.getDatapointsList()) {
      seriesData.push([dataPoint.getTimestamp(), dataPoint.getValue()]);
    }
    return {
        name: data.getName(), 
        data: seriesData, 
        color: data.getColor() || undefined, 
        hidden: data.isHidden()
    };
  }

  private async transformEvent2Annotation(data: TimeSeriesData){
    let eventData = [];
    for await (const event of data.getEventsList()){
      let bgcolor = event.getBackgroundColor() || "#000";
      let textColor = event.getTextColor() || "#fff";
      eventData.push({
        x: event.getTimestamp().getTime(),
        borderColor: bgcolor,
        label: {
          show: event.isShow(),
          text: event.getName(),
          style: { color: textColor, background: bgcolor }
        }
      });
    }
    return eventData;
  }

  public toggleLiveData(event: any) {
    this.liveData = !this.liveData;
  }

  public getToggleLiveDataButtonLabel(): string {
    return this.liveData ? "Data is live" : "Data is paused";
  }

  // API for external control
  @Input()
  public set showAfter(val: any) {
      if (this.chart && val) {
          let time = val;
          if (val instanceof Date) {
              time = val.getTime();
          }
          this.chart.updateOptions({ xaxis: { min: time } }, false, false);
      }
  }

  @Input()
  public set showUntil(val: any) {
      if (this.chart && val) {
          let time = val;
          if (val instanceof Date) {
              time = val.getTime();
          }
          this.chart.updateOptions({ xaxis: { max: time } }, false, false);
      }
  }
}

GemTimeSeriesChart["__class"] = "gui.viz.TimeSeriesChart2";

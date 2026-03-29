/* (c) https://github.com/MontiCore/monticore */
import {Component, ElementRef, ViewChild, AfterViewInit, OnDestroy, HostBinding, Input} from '@angular/core';
import {GemTimeSeriesChartComponent, config} from '../../../../../../build/gui2java2ts/mc/fenix/charts/GemTimeSeriesChartComponent';
import ApexCharts from 'apexcharts';
import { TimeSeriesData } from '../../../../../../build/gui2java2ts/mc/fenix/charts/gemtimeseriescharttypes/TimeSeriesData';
import { TimeSeries } from '../../../../../../build/gui2java2ts/mc/fenix/charts/gemtimeseriescharttypes/TimeSeries';

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
  private resizeTimeout: any;
  private chartId: string = 'ts-chart-' + Math.random().toString(36).substr(2, 9);
  private userMinX: number | null = null;
  private userMaxX: number | null = null;
  private lastUpdateTimestamp: number = 0;
  private isFirstLoad: boolean = true;

  constructor(private hostElement: ElementRef) {
    super();
  }

  private toListArray<T>(list: any): T[] {
    if (!list) return [];
    if (Array.isArray(list)) return list;
    if (typeof list.toArray === 'function') return list.toArray();
    const count = (typeof list.size === 'function') ? list.size() : (list.length || 0);
    const arr = [];
    for (let i = 0; i < count; i++) {
      arr.push((typeof list.get === 'function') ? list.get(i) : list[i]);
    }
    return arr;
  }

  public async init() {
    await super.init();

    this.options = {
      noData: { text: "Loading..." },
      dataLabels: { enabled: false },
      chart: { 
        id: this.chartId, 
        type: 'area', 
        height: '100%', 
        width: '100%',
        animations: {
            enabled: true,
            easing: 'linear',
            dynamicAnimation: {
                speed: 800
            }
        },
        events: {
          click: (event, chartContext, config) => {
            const target = event.target;
            if (target && (target.classList.contains('gem-clickable-annotation') || target.closest('.apexcharts-xaxis-annotations'))) {
                const textContent = target.textContent;
                if (textContent && this.getEventClick()) {
                    const data = this.getData();
                    if (data) {
                        const eventsList = this.toListArray<any>(data.getEventsList());
                        for (const evt of eventsList) {
                            if (evt.getName().trim() === textContent.trim()) {
                                this.getEventClick()(evt);
                                break;
                            }
                        }
                    }
                }
            }
          },
          zoomed: (chartContext, { xaxis }) => {
            if (Date.now() - this.lastUpdateTimestamp < 1000) return;
            const globals = chartContext.w.globals;
            // Only update if it's a real zoom (range is smaller than full range)
            // or if we explicitly want to clear it (Home button)
            if (globals.isUserZoomed) {
              this.userMinX = xaxis.min || null;
              this.userMaxX = xaxis.max || null;
            } else {
              // Only clear if we are not currently updating and globals say no zoom
              this.userMinX = null;
              this.userMaxX = null;
            }
          },
          scrolled: (chartContext, { xaxis }) => {
            if (Date.now() - this.lastUpdateTimestamp < 1000) return;
            const globals = chartContext.w.globals;
            if (globals.isUserZoomed) {
              this.userMinX = xaxis.min || null;
              this.userMaxX = xaxis.max || null;
            }
          }
        }
      },
      series: [],
      xaxis: { type: 'datetime' },
      yaxis: {
        labels: {
          formatter: (val) => (val !== null && val !== undefined) ? val.toFixed(2) : val
        }
      }
    };

    if (this.targetElement) {
      this.chart = new ApexCharts(this.targetElement.nativeElement, this.options);
      this.chart.render();
      if (this.getData()) {
        this.processNewData(this.getData());
      }
    }
  }

  ngAfterViewInit() {
    this.resizeObserver = new ResizeObserver(() => {
      if (this.resizeTimeout) {
        clearTimeout(this.resizeTimeout);
      }
      this.resizeTimeout = setTimeout(() => {
        if (this.chart) {
          this.lastUpdateTimestamp = Date.now();
          const updateParams: any = { chart: { height: '100%', width: '100%' } };
          if (this.userMinX !== null && this.userMaxX !== null) {
            updateParams.xaxis = { min: this.userMinX, max: this.userMaxX };
          }
          this.chart.updateOptions(updateParams, false, false);
        }
      }, 100);
    });
    this.resizeObserver.observe(this.hostElement.nativeElement);
  }

  ngOnDestroy() {
    if (this.resizeTimeout) clearTimeout(this.resizeTimeout);
    if (this.resizeObserver) this.resizeObserver.disconnect();
    if (this.chart) this.chart.destroy();
  }

  public setData(val: TimeSeriesData) {
    super.setData(val);
    if (this.liveData) {
      this.processNewData(val);
    }
  }

  private async processNewData(data: TimeSeriesData): Promise<void> {
    if (!this.chart || !data) return;

    const newSeries = [];
    const seriesList = this.toListArray<TimeSeries>(data.getSeriesList());

    for (const series of seriesList) {
      let seriesData = [];
      const dpList = this.toListArray<any>(series.getDatapointsList());
      
      for (const dp of dpList) {
          const timestamp = dp.getTimestamp();
          if (timestamp) {
            const time = (typeof timestamp.getTime === 'function') ? timestamp.getTime() : new Date(timestamp).getTime();
            seriesData.push([time, dp.getValue()]);
          }
      }

      newSeries.push({
        name: series.getName(),
        data: seriesData,
        color: series.getColor() || undefined
      });
    }

    // Process Events
    const annotationsX = [];
    const eventsList = this.toListArray<any>(data.getEventsList());
    for (const event of eventsList) {
      if (event.isShow()) {
        const timestamp = event.getTimestamp();
        const time = (typeof timestamp.getTime === 'function') ? timestamp.getTime() : new Date(timestamp).getTime();
        annotationsX.push({
          x: time,
          borderColor: event.getBackgroundColor() || '#FEB019',
          label: {
            borderColor: event.getBackgroundColor() || '#FEB019',
            style: {
              color: event.getTextColor() || '#fff',
              background: event.getBackgroundColor() || '#FEB019',
              cssClass: 'gem-clickable-annotation'
            },
            text: event.getName(),
          }
        });
      }
    }

    this.options.series = newSeries;
    this.options.annotations = { xaxis: annotationsX };

    await this.refreshChart();
  }

  private async refreshChart() {
    if (!this.chart) return;
    
    this.lastUpdateTimestamp = Date.now();
    const updateParams: any = { 
      series: this.options.series,
      annotations: this.options.annotations
    };

    // Determine x-axis range (ViewPort or manual Zoom)
    let minX = this.userMinX;
    let maxX = this.userMaxX;

    const vp = this.getViewPort();
    if (minX === null && vp && vp.length > 0) {
      const match = vp.match(/^(\d+)([shmp])$/);
      if (match) {
        const value = parseInt(match[1]);
        const unit = match[2];

        let latestX = -1;
        for (const s of this.options.series) {
          if (s.data && s.data.length > 0) {
            const lastPoint = s.data[s.data.length - 1];
            const lx = Array.isArray(lastPoint) ? lastPoint[0] : lastPoint.x;
            if (lx > latestX) latestX = lx;
          }
        }

        if (latestX !== -1) {
          if (unit === 'p') {
            // Points-based window: Find earliest timestamp of the last N points across all series
            let targetMinX = latestX;
            for (const s of this.options.series) {
              if (s.data && s.data.length > 0) {
                const idx = Math.max(0, s.data.length - value);
                const p = s.data[idx];
                const px = Array.isArray(p) ? p[0] : p.x;
                if (px < targetMinX) targetMinX = px;
              }
            }
            minX = targetMinX;
            maxX = latestX;
          } else {
            // Time-based window
            let millis = value * 1000;
            if (unit === 'm') millis *= 60;
            if (unit === 'h') millis *= 3600;
            
            maxX = latestX;
            minX = maxX - millis;
          }

          updateParams.xaxis = { min: minX, max: maxX };
        }
      }
    } else if (minX !== null && maxX !== null) {
      updateParams.xaxis = { min: minX, max: maxX };
    }

    // Using updateOptions with redrawPaths=false for smoother appending animations
    // in live scenarios
    const shouldAnimate = !this.isFirstLoad;
    this.chart.updateOptions(updateParams, false, shouldAnimate, false);
    this.isFirstLoad = false;

    // Hard override if we have user zoom or viewPort
    if (minX !== null && maxX !== null) {
      this.chart.zoomX(minX, maxX);
    }  }

  public toggleLiveData(event: any) {
    this.liveData = !this.liveData;
    if (this.liveData && this.getData()) {
      this.processNewData(this.getData());
    }
  }

  public getToggleLiveDataButtonLabel(): string {
    return this.liveData ? "Pause Live Stream" : "Resume Live Stream";
  }

  @Input()
  public set showAfter(val: Date) {
      if (this.chart) {
          const time = (val && typeof (val as any).getTime === 'function') ? (val as any).getTime() : val;
          this.chart.updateOptions({ xaxis: { min: time } }, false, false);
      }
  }

  @Input()
  public set showUntil(val: Date) {
      if (this.chart) {
          const time = (val && typeof (val as any).getTime === 'function') ? (val as any).getTime() : val;
          this.chart.updateOptions({ xaxis: { max: time } }, false, false);
      }
  }
}

// Marker class for JSON deserialization in RTE
GemTimeSeriesChart["__class"] = "gui.viz.TimeSeriesChart2";

/* (c) https://github.com/MontiCore/monticore */
import {Component, ElementRef, ViewChild} from '@angular/core';
import {GemTimeSeriesChartComponent, config} from '../../../../../../build/gui2java2ts/mc/fenix/charts/GemTimeSeriesChartComponent';
import {OnDestroy} from '@angular/core';
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
export class GemTimeSeriesChart extends GemTimeSeriesChartComponent implements OnDestroy {

  options: any;
  //targetElement: any;
  chart: ApexCharts;
  @ViewChild('tSChart') targetElement! : ElementRef<HTMLDivElement>;
  private liveData: boolean = true;

  public async init() {
    await super.init();

    this.options = {
      animations: {
        enabled: false,
        speed: 800,
        animateGradually: {
          enabled: true,
          delay: 150
        },
        dynamicAnimation: {
          enabled: true,
          speed: 350
        }
      },
      noData: {
        text: true ? "Loading...":"No Data present in the graph!",
        align: 'center',
        verticalAlign: 'middle',
        offsetX: 0,
        offsetY: 0,
        style: {
          color: "#000000",
          fontSize: '14px',
          fontFamily: "Helvetica"
        }
      },
      chart: {
        id: 'area-datetime',
        type: 'area',
        height: 350,
        width: '75%',/**/
        zoom: {
          autoScaleYaxis: true
        },
        events: {
          legendClick: function(chartContext, seriesIndex, opts) {
            this.options.series[seriesIndex].hidden = !this.options.series[seriesIndex].hidden;
          }.bind(this)
        }
      },
      series: [],
      xaxis: {
        type: 'datetime',
        // min: new Date('01 Mar 2012').getTime(),
        tickAmount: 6,
        lables: {
          format: 'dd MMM yyyy HH:mm:ss',
          formatter: (value) => {
            const date: Date = new Date(value);
            return date.toLocaleDateString("de-DE", {timeZone: "Europe/Berlin"})
          }
        }
      },
      annotations: {
        yaxis: [],
        xaxis: []
      },

      dataLabels: {
        enabled: false
      },
      markers: {
        size: 0,
        style: 'hollow',
      },
      tooltip: {
        x: {
          format: 'dd MMM yyyy HH:mm:ss',
          formatter: (value) => {
            const date = new Date(value);
            return date.toLocaleString("de-DE", { timeZone: "Europe/Berlin" });
          }
        }
      },
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
      this.chart = new ApexCharts(this.targetElement.nativeElement, this.options); // Neues ApexChart-Objekt instanziieren
      this.chart.render(); // Rendern des Charts
    } else {
      console.error("Konnte das Element mit der ID 'apx-chart' nicht finden.");
    }
  }

  public setData(val: TimeSeriesData) {
    super.setData(val);
    this.processNewData(val);
  }

  id2TimeSeriesIndex: Map<number, number> = new Map();

  private async processNewData(data: TimeSeriesData): Promise<void> {
    for await (const series of data.getSeriesList()) {
      if (this.id2TimeSeriesIndex.has(series.getIdentifier())) {
        this.options.series[this.id2TimeSeriesIndex.get(series.getIdentifier())] = await this.transformData2Series(series);
      } else {
        this.id2TimeSeriesIndex.set(series.getIdentifier(), this.options.series.length);
        this.options.series.push(await this.transformData2Series(series));
      }
    }

    if (this.liveData) {
      console.log("updateSeries");
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

    let newSeries: {name: string, data: any[], color?: string, hidden?: boolean};
    if (data.getColor() == null) {
      newSeries = {name: data.getName(), data: seriesData, hidden: data.isHidden()};
    } else {
      newSeries = {name: data.getName(), data: seriesData, color: data.getColor(), hidden: data.isHidden()};
    }
    return newSeries;
  }

  private async transformEvent2Annotation(data: TimeSeriesData){
    let eventData = [];
    for await (const event of data.getEventsList()){
      let bgcolor = event.getBackgroundColor();
      if (bgcolor == null) bgcolor="#000"
      let textColor = event.getTextColor();
      if (textColor == null) textColor = "#fff"
      let annotation = {x: event.getTimestamp().getTime(),
        borderColor: bgcolor,
        yAxisIndex: 0,
        label: {
          show: event.isShow(),
          text: event.getName(),
          style: {
            color: textColor,
            background: bgcolor
          }
        }}
      eventData.push(annotation);
    }
    return eventData
  }

  public toggleLiveData(event: any) {
    console.log("toggleLiveData", this.liveData);
    this.liveData = !this.liveData;
  }

  public getToggleLiveDataButtonLabel(): string {
    return this.liveData ? "Data is live" : "Data is paused";
  }

  public setShowAfter(event: any) {
    console.log("setShowAfter", event);

    if (event && event instanceof Date) {
      const date: Date = event;
      this.options.xaxis.min = date.getTime();
      this.chart.updateOptions({
        xaxis: this.options.xaxis
      })
    } else {
      this.options.xaxis.min = null;
      this.chart.updateOptions({
        xaxis: this.options.xaxis
      })
    }
  }

  // Eigenschaften für showBefore
  beforeDateEntry: string = ''; // Vorheriges Datum
  beforeTimeEntry: string = '0:00'; // Vorherige Uhrzeit

  // Eigenschaften für showAfter (bereits vorhanden)
  afterDateEntry: string = '';
  afterTimeEntry: string = '0:00';


  // Methoden für showBefore
  onBeforeDateInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.beforeDateEntry = input.value;

    this.updateChartWithSelectedDateTime();
  }

  onBeforeTimeInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.beforeTimeEntry = input.value;

    this.updateChartWithSelectedDateTime();
  }

  // Methoden für showAfter (bereits implementiert)
  onAfterDateInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.afterDateEntry = input.value;

    this.updateChartWithSelectedDateTime();
  }

  onAfterTimeInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.afterTimeEntry = input.value;

    this.updateChartWithSelectedDateTime();
  }

  // Aktualisiert das Diagramm basierend auf kombiniertem Datum und Uhrzeit
  private updateChartWithSelectedDateTime(): void {
    if (this.afterDateEntry && this.afterTimeEntry) {
      // Datum und Uhrzeit kombinieren
      const [year, month, day] = this.afterDateEntry.split('-').map(Number);
      const [hours, minutes] = this.afterTimeEntry.split(':').map(Number);

      const selectedDateTime = new Date(year, month - 1, day, hours, minutes);
      this.options.xaxis.min = selectedDateTime.getTime();

      // Optional: Diagramm neu rendern, wenn die Optionen geändert wurden
      if (this.chart) {
        this.chart.updateOptions(this.options, true, true);
      }
    } else {
      console.warn('Ungültige Eingaben. Datum oder Uhrzeit fehlt.');
    }

    if (this.beforeDateEntry && this.beforeTimeEntry) {
      // Datum und Uhrzeit kombinieren
      const [year, month, day] = this.beforeDateEntry.split('-').map(Number);
      const [hours, minutes] = this.beforeTimeEntry.split(':').map(Number);

      const selectedDateTime = new Date(year, month - 1, day, hours, minutes);
      this.options.xaxis.max = selectedDateTime.getTime();

      // Optional: Diagramm neu rendern, wenn die Optionen geändert wurden
      if (this.chart) {
        this.chart.updateOptions(this.options, true, true);
      }
    } else {
      console.warn('Ungültige Eingaben. Datum oder Uhrzeit fehlt.');
    }
  }

  constructor() {
    super();
  }
}

GemTimeSeriesChart["__class"] = "gui.viz.TimeSeriesChart2";

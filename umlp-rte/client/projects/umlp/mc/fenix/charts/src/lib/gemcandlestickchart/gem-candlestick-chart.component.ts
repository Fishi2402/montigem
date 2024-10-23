import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { GemCandlestickChart as GemCandlestickChartTOP } from './gem-candlestick-chart/gem-candlestick-chart.component-gen';
import { GemData2CandlestickChart } from '../gen/gemcandlestickcharttypes/GemData2CandlestickChart';
import * as moment from 'moment';

@Component({
  selector: 'gem-candlestick-chart',
  templateUrl: './gem-candlestick-chart.component.html',
  styleUrls: ['./gem-candlestick-chart.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GemCandlestickChart extends GemCandlestickChartTOP {

  constructor() {
    super();
  }

  ngOnInit(): void {
  }

  /**
   * Connected with the *ngIf Directive to show/ hide the external HTML buttons for controlling the range of the chart
   */
  period: boolean = false;

  /**
   * Connected with the *ngIf Directive to show/ hide the external HTML buttons for controlling the date formatting of the chart
   */
  dateformat: boolean = false;

  /**
   * Connected with the *ngIf Directive to show/ hide the external HTML buttons for controlling the type of the chart
   */
  charttype: boolean = false;

  rightmargin: number = 0;
  topmargin: number = 20;
  bottommargin: number = 0;
  leftmargin: number = 50;
  fourclicked: boolean = false;

  /**
   * Set to true to show the built-in buttons of plotly.js, false to hide them.
   */
  builtinbuttons: boolean = false;

  /**
   * Default color for decreasing values
   */
  decreasing: string = '#cc071e';

  /**
   * Default color for increasing values
   */
  increasing: string = '#11750e';

  /**
   * Contains information about the chart, which is passed to plotly.js, such as:
   * data, type of the chart, title, font sizes, colors etc.
   */
  _data: any = [
    {
      type: "candlestick",
      x: [],
      close: [],
      open: [],
      high: [],
      low: [],
      decreasing: { line: { color: this.decreasing } },
      increasing: { line: { color: this.increasing } },
      xaxis: 'x',
      yaxis: 'y'
    }
  ];

  /**
   * Contains information about the chart, which is passed to plotly.js, such as:
   * buttons of the chart, margins etc.
   */
  _layout = {
    newlayout: null,
    dragmode: 'zoom',
    margin: {
      r: this.rightmargin,
      t: this.topmargin,
      b: this.bottommargin,
      l: this.leftmargin
    },
    xaxis: {
      range: [],
      tickformat: null,
      rangeslider: { visible: true },
      rangeselector: { buttons: this.showbuttons() },
      //title: 'Date',
      type: 'date'
    },
    yaxis: {
      autorange: true,
      type: 'linear'
    }
  };

  /**
   * Checks if built-in buttons are on or off.
   * @returns an array of pre-defined buttons if on, an empty array otherwise
   */
  public showbuttons(): any[] {
    if (this.builtinbuttons) {
      return [
        {
          step: 'day',
          stepmode: 'backward',
          count: 7,
          label: '1 week'
        },
        {
          step: 'day',
          stepmode: 'backward',
          count: 14,
          label: '2 weeks'
        },
        {
          step: 'month',
          stepmode: 'backward',
          count: 1,
          label: '1 month'
        },
        {
          step: 'month',
          stepmode: 'backward',
          count: 3,
          label: '3 months'
        },
        {
          step: 'month',
          stepmode: 'backward',
          count: 6,
          label: '6 months'
        },
        {
          step: 'year',
          stepmode: 'backward',
          count: 1,
          label: '1 year'
        },
        {
          step: 'all',
          label: 'All dates'
        }
      ]
    } else {
      return [];
    }
  }

  /**
   * Updates the layout of the chart.
   * @param range Zoom the chart in/out to focus on the passed {@link range}
   * @param tick Set the format of the dates, shown on the x axis
   */
  public layout(range: string[], tick: string) {
    this._layout = {
      newlayout: null,
      dragmode: 'zoom',
      margin: {
        r: this.rightmargin,
        t: this.topmargin,
        b: this.bottommargin,
        l: this.leftmargin
      },
      xaxis: {
        range: range,
        tickformat: tick,
        rangeslider: { visible: true },
        rangeselector: {
          buttons: this.showbuttons()
        },
        //title: 'Date',
        type: 'date'
      },
      yaxis: {
        autorange: true,
        type: 'linear'
      }
    };
  }

  /**
   * Update the layout of the chart.
   * Intended for html button inputs.
   * @param update Default values: If the parameter update == 1, the chart zooms in/out to the last week of inputs.
   * If update == 2 || 3, the format of the dates, presented on the x axis, changes.
   * If update == 4 , the type of the chart changes (between ohlc and candlestick).
   * Update == 5-10 adapts the range of the chart.
   */
  public updatelayout(update: number) {
    if (update == 1) {
      let lastX = this._data[0].x[this._data[0].x.length - 1];
      let newFirstDate: moment.Moment = this.parse(lastX);
      let newLastDate: moment.Moment = this.parse(lastX);
      /* To add new buttons for different zoom ranges (e.g. showing only the last 2 weeks), simply copy the code
         inside this if statement and change the 7 at the end of the following line
         (in the same example to 14) and make the buttons call this function with values 5, 6 etc.
      */
      newFirstDate.date(newFirstDate.date() - 7);
      let firstDate = this.format(newFirstDate);
      let lastDate = this.format(newLastDate);

      this.layout([firstDate, lastDate], this._layout.xaxis.tickformat);
    }
    else if (update == 2) {
      //this.layout(this._layout.xaxis.range, "%b %d, %Y");
      this._layout.xaxis.tickformat = "%b %d, %Y";
      this._layout.newlayout = "%b %d, %Y";
    }
    else if (update == 3) {
      //this.layout(this._layout.xaxis.range, "%d/%m/%Y");
      this._layout.xaxis.tickformat = "%d/%m/%Y";
      this._layout.newlayout = "%d/%m/%Y";
    }
    else if (update == 4) {
      if (this.fourclicked) {
        this._data[0].type = "candlestick";
        this.fourclicked = false;
      }
      else {
        this._data[0].type = "ohlc";
        this.fourclicked = true;
      }
    }
    else if (update >= 5 && update <= 10) {
      let mapping = {
        5: 14,
        6: 31,
        7: 91,
        8: 183,
        9: 365,
        10: this._data[0].x.length
      }
      let lastX = this._data[0].x[this._data[0].x.length - 1];
      let newDate: moment.Moment = this.parse(lastX);
      let newlastDate: moment.Moment = this.parse(lastX);
      newDate.date(this.parse(lastX).date() - mapping[update]);
      let firstDate = this.format(newDate);
      let lastDate = this.format(newlastDate);

      if (this._layout.xaxis.range[0] != firstDate) {
        this.layout([firstDate, lastDate], this._layout.xaxis.tickformat);
      }
    }
  }

  protected parse(date: string): moment.Moment {
    return moment(date, "YY-MM-DD");
  }

  protected format(date: moment.Moment): string {
    return date.format("YY-MM-DD");
  }

  /**
   * Setter for chart's type (ohlc or candlestick).
   */
  @Input()
  public set type(type: string) {
    if (type != null) {
      if (type == "ohlc" || type == "candlestick") {
        this._data[0].type = type;
      }
      else console.log("Only ohlc and candlestick are supported types");
    }
    else {
      this._data[0].type = 'candlestick';
    }
  }

  /**
   * Used to configure which buttons are shown.
   * @param buttons If false, no buttons are shown. If true, preconfigured built-in Plotly.js buttons for controlling the range are displayed.
   * Pass 'period' to display only the external HTML buttons for adapting the time period,
   * 'dateformat' for the buttons for adapting the date formatting and 'type' for the button, switching the type of the chart.
   * To use any combination of them, pass a combined with +'s string, like e.g 'period+dateformat+type'.
   */
  @Input()
  public set buttons(buttons: boolean | string) {
    if (buttons == true) {
      this.builtinbuttons = true;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.charttype = false;
      this.period = false;
      this.charttype = false;
    }
    else if (buttons == false) {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.charttype = false;
      this.period = false;
      this.charttype = false;
    }
    else if (buttons == 'period') {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.period = true;
      this.charttype = false;
      this.dateformat = false;
    }
    else if (buttons == 'dateformat') {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.dateformat = true;
      this.charttype = false;
      this.period = false;
    }
    else if (buttons == 'type') {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.charttype = true;
      this.period = false;
      this.charttype = false;
    }
    else if (buttons == 'period+dateformat' || buttons == 'dateformat+period') {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.dateformat = true;
      this.period = true;
      this.charttype = false;
    }
    else if (buttons == 'period+type' || buttons == 'type+period') {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.dateformat = false;
      this.period = true;
      this.charttype = true;
    }
    else if (buttons == 'period+dateformat+type' || buttons == 'period+type+dateformat'
      || buttons == 'type+dateformat+period' || buttons == 'type+period+dateformat'
      || buttons == 'dateformat+period+type' || buttons == 'dateformat+type+period') {
      this.builtinbuttons = false;
      this.layout(this._layout.xaxis.range, this._layout.xaxis.tickformat);
      this.dateformat = true;
      this.period = true;
      this.charttype = true;
    }
  }

  /**
   * Setter for the colors of the decreasing (colors[0]) and increasing (colors[1]) lines.
   */
  @Input()
  public set colors(colors: string[]) {
    if (colors != null && colors.length == 2) {
      this._data[0].decreasing.line.color = colors[0];
      this._data[0].increasing.line.color = colors[1];
    }
    else {
      this._data[0].decreasing.line.color = this.decreasing;
      this._data[0].increasing.line.color = this.increasing;
    }
  }

  /**
   * Setter for the data of the chart.
   * The correct order for values in the value property of each object is:
   * open, high, low, close
   */
  @Input()
  public set data(data: GemData2CandlestickChart[]) {
    if (!data) {
      return;
    }
    //this.layout(null, null);
    for (let index = 0; index < data.length; index++) {
      this._data[0].x.push(data[index].getLabel());
      this._data[0].open.push(data[index].getValue(0));
      this._data[0].high.push(data[index].getValue(1));
      this._data[0].low.push(data[index].getValue(2));
      this._data[0].close.push(data[index].getValue(3));
    }
  }

  /**
   * Setter for the margins of the chart
   * If no input is recieved, the default margins are used.
   * @param margins is a number or an array containing of between one and four numbers (just like the CSS margin property):
   * 10 or [10] would set each margin to 10;
   * [10,20] would set the top and bottom margins to 10, but the left and right margins to 20;
   * [10,20,30] would set the top margin to 10, the left and right margins to 20, and the bottom one to 30.
   * [10,20,30,40] would set the top margin to 10, the right to 20, bottom to 30 and left to 40.
   */
  @Input()
  public set margins(margins: number[] | number) {
    if (margins != null) {
      if (typeof margins == "number") {
        this._layout.margin.t = margins;
        this._layout.margin.b = margins;
        this._layout.margin.l = margins;
        this._layout.margin.r = margins;
      }
      else {
        if (margins.length == 1) {
          this._layout.margin.t = margins[0];
          this._layout.margin.b = margins[0];
          this._layout.margin.l = margins[0];
          this._layout.margin.r = margins[0];
        }
        else if (margins.length == 2) {
          this._layout.margin.t = margins[0];
          this._layout.margin.b = margins[0];
          this._layout.margin.l = margins[1];
          this._layout.margin.r = margins[1];
        }
        else if (margins.length == 3) {
          this._layout.margin.t = margins[0];
          this._layout.margin.b = margins[2];
          this._layout.margin.l = margins[1];
          this._layout.margin.r = margins[1];
        }
        else if (margins.length == 4) {
          this._layout.margin.t = margins[0];
          this._layout.margin.b = margins[2];
          this._layout.margin.l = margins[3];
          this._layout.margin.r = margins[1];
        }
        else console.warn("Wrong format of the margins data");
      }
    }
    else {
      this._layout.margin.t = this.topmargin;
      this._layout.margin.b = this.bottommargin;
      this._layout.margin.l = this.leftmargin;
      this._layout.margin.r = this.rightmargin;
    }
  }
}

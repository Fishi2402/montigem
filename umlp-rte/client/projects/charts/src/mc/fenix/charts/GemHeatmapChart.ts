/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, SimpleChanges } from '@angular/core';
import { config, GemHeatmapChartComponent } from '../../../../build/gui/mc/fenix/charts/GemHeatmapChartComponent';
import { NgxChartsModule, ScaleType } from "@swimlane/ngx-charts";
import { GemChartOptions } from './GemChartOptions';
import { GemHeatmapChartData } from '../../../../build/cd/mc/fenix/charts/gemheatmapcharttypes/GemHeatmapChartData';

@Component({
  standalone: true,
  imports: [...config.imports, NgxChartsModule, GemChartOptions, NgxChartsModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemHeatmapChart extends GemHeatmapChartComponent {

  public _data: GemHeatmapChartData;
  public _xLabel: string;
  public _yLabel: string;
  public _timestampdata = false;
  public _hasGroupingFactor = false;

  @Input() defaultColor?: string = "#303C46";
  @Input() defaultColNum?: number = 6;


  /**
   * Updates internal parameters based on Inputs
   */
  ngOnChanges(changes: SimpleChanges) {
    for (const propName in changes) {
      if (changes.hasOwnProperty(propName)) {
        switch (propName) {
          case 'data': {
            if (this.data) this.computed_data = this.rearrange_array(this.data.getEntriesList().map(
              e => { return { name: e.getName(), value: e.getTimestamp() } }
            ));
            break;
          }
          case 'timestampdata': {
            if (this.timestampdata) {
              this.prettyprint_timestamps = true;
              this.loop_factor = 86400;
            }
            break;
          }
          case 'defaultColor': {
            if (/^#[0-9A-F]{6}$/i.test(this.defaultColor)) this.enter_color(this.defaultColor);
            break;
          }
          case 'defaultColNum': {
            this.enter_num_seperations(this.defaultColNum);
          }
        }
      }
    }
  }



  /**
   * @param min_num_seperations The lower bound for the number of columns in the chart
   * @param max_num_seperations The upper bound for the number of columns in the chart
   */
  private min_num_seperations = 1;
  private max_num_seperations = 32;

  /**
   * @param computed_data Data used as input for [NGX-Charts Heatmap]{@link https://swimlane.gitbook.io/ngx-charts/v/docs-test/examples/heat-map-chart}
   * @example * [
   *{
   *  "name": "Germany",
   *  "series": [
   *    {
   *      "name": "2010",
   *      "value": 7300000
   *    },
   *    {
   *      "name": "2011",
   *      "value": 8940000
   *    }
   *  ]
   * },
   * {
   *  "name": "USA",
   *  "series": [
   *    {
   *      "name": "2010",
   *      "value": 7870000
   *    },
   *    {
   *      "name": "2011",
   *      "value": 8270000
   *    }
   *  ]
   * }
   *]
   */
  computed_data;

  /**
   * Sets some Input values for [NGX-Charts Heatmap]{@link https://swimlane.gitbook.io/ngx-charts/v/docs-test/examples/heat-map-chart}
   * Specifically @param showXAxis, @param showYAxis, @param gradient, @param displayLegend, @param showXAxisLabel, @param showYAxisLabel
   */
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  displayLegend = true;
  showXAxisLabel = false;
  showYAxisLabel = false;




  /**
   * @param colorScheme The default color-scheme: Result of running enter_color on default color #303C46
   */
  colorScheme = {
    name: 'default',
    selectable: true,
    group: ScaleType.Linear,
    domain: ['#A3CAEC', '#98BCDB', '#8CAECB', '#819FBA', '#7591AA', '#6A8399', '#5E7588', '#536778', '#536778', '#3C4A57', '#303C46']
  };
  /**
   * @param colorscalefactor The secondary color is calculated as being colorscalefactor away from white/black when starting at the selected color
   */
  colorscalefactor = 0.3;
  /**
   * @param color_domain_mid_steps How many mid-steps there should in the colorScheme between selected and secondary color. (Due to a Bug of mgx-charts this shouldn't be low, as otherwise the bin-colors overshoot the maximum color)
   */
  color_domain_mid_steps = 10;

  /**
   * Creates a new @param colorScheme based on @param value (Is influenced by @param colorscalefactor and @param color_domain_mid_steps)
   * @param value A Hex Color String
   */
  enter_color(value: string) {
    //Converts Hex-String to RGB values
    var color = parseInt(value.substr(1), 16);
    var blue = (color % 256);
    var green = ((color - blue) / 256) % 256;
    var red = (color - blue - (256 * green)) / 65536;

    //Checks if the given color is closer to white or black, then selects the secondary color as 1-colorscalefactor the way towards the further of the two
    if ((blue + green + red) > 381) {
      var secondary_red = red * this.colorscalefactor;
      var secondary_green = green * this.colorscalefactor;
      var secondary_blue = blue * this.colorscalefactor;
    }
    else {
      var secondary_red = 255 - (255 - red) * this.colorscalefactor;
      var secondary_green = 255 - (255 - green) * this.colorscalefactor;
      var secondary_blue = 255 - (255 - blue) * this.colorscalefactor;
    }

    //Adds colorscalefactor-1 linear mid-steps between secondary color and input (For colorscalefactor=1 there is only secondary color and 2*input
    var new_domain = [];
    for (var i = 0; i < this.color_domain_mid_steps; i++) {
      new_domain.push(this.color_to_html_hex(((this.color_domain_mid_steps - i) * secondary_red + i * red) / this.color_domain_mid_steps, ((this.color_domain_mid_steps - i) * secondary_green + i * green) / this.color_domain_mid_steps, ((this.color_domain_mid_steps - i) * secondary_blue + i * blue) / this.color_domain_mid_steps));
    }
    new_domain.push(value);
    //Added a second time to aprehend ngx bug
    new_domain.push(value);
    this.colorScheme = {
      name: 'default',
      selectable: true,
      group: ScaleType.Linear,
      domain: new_domain
    };
  }

  /**
   * @param redv Red value (0-255)
   * @param greenv Green value (0-255)
   * @param bluev  Blue value (0-255)
   * @returns Hex Color String from the given color values
   */
  color_to_html_hex(redv, greenv, bluev) {
    return ('#' + ('0' + Math.floor(redv).toString(16)).slice(-2) + ('0' + Math.floor(greenv).toString(16)).slice(-2) + ('0' + Math.floor(bluev).toString(16)).slice(-2));
  }




  /**
   * @param num_seperations number of columns in the chart
   */
  num_seperations = 6;
  /**
   * @param loop_factor A modulo value for binning
   */
  loop_factor: number = 10;
  /**
   * @param prettyprint_timestamps Wether the values on the X-Axis should be converter to Timestamp-Strings
   */
  prettyprint_timestamps = false;
  /**
   * @param loop_days Wether a modulo should be applied to the values of the entries in @param data
   */
  loop_days = false;

  /**
   * Updates internal parameters @param num_seperations and  @param computed_data based on User-Inputs
   * @param value Number entered by the used
   */
  enter_num_seperations(value: string | number) {
    if (typeof value === "number") {
      if (value < this.min_num_seperations) { this.num_seperations = this.min_num_seperations; }
      else if (value > this.max_num_seperations) { this.num_seperations = this.max_num_seperations; }
    } else { this.num_seperations = +value; }
    if (this.data) this.computed_data = this.rearrange_array(this.data.getEntriesList());
  }

  /**
   * Updates internal parameters @param loop_factor and  @param computed_data based on User-Inputs
   * @param value Number entered by the used
   */
  enter_num_loop_factor(value: string | number) {
    if (typeof value === "number") {
      if (value < 2) this.loop_factor = 2;
    }
    else this.loop_factor = +value;
    if (this.data) this.computed_data = this.rearrange_array(this.data.getEntriesList());
  }

  /**
   * Updates internal parameters @param prettyprint_timestamps and @param computed_data based on User-Inputs
   * @param value Checkbox checked by User
   */
  enter_pretty_date(value) {
    this.prettyprint_timestamps = value;
    if (this.data) this.computed_data = this.rearrange_array(this.data.getEntriesList());
  }

  /**
   * Updates internal parameters @param loop_days and  @param computed_data based on User-Inputs
   * @param value Checkbox checked by User
   */
  enter_loop_days(value) {
    this.loop_days = value;
    if (this.data) this.computed_data = this.rearrange_array(this.data.getEntriesList());
  }


  /**
   * @param somedata A list of entries. Just like @param data
   * @returns a matrix of entries. Just like @param computed_data
   */
  //Creates an [{"name",series:["name","value"]}] array from an [{"name", "timestamp"}] array by splitting the time into num_seperators equal periods and couting amount of entries for each period & sensor name
  rearrange_array(somedata) {
    if (!somedata[0]) return [];
    else {
      if (this.timestampdata && !('timestamp' in somedata[0])) return [];
      if (!this.timestampdata && !('value' in somedata[0])) return [];
    }

    //Depending on input type this either uses "timestamp" or "value" fields
    if (this.timestampdata) {
      //Earliest timestamp
      var min = somedata[0].timestamp;
      //Latest timestamp
      var max = somedata[0].timestamp;
      //List of Sensor Names
      var names = [];
      //Calculate the three values
      for (var i_data of somedata) {
        if (min > i_data.timestamp) { min = i_data.timestamp; }
        if (max < i_data.timestamp) { max = i_data.timestamp; }
        names.indexOf(i_data.name) === -1 ? names.push(i_data.name) : undefined;
      };
    }
    else {
      //Earliest timestamp
      var min = somedata[0].value;
      //Latest timestamp
      var max = somedata[0].value;
      //List of Sensor Names
      var names = [];
      //Calculate the three values
      for (var i_data of somedata) {
        if (min > i_data.value) { min = i_data.value; }
        if (max < i_data.value) { max = i_data.value; }
        names.indexOf(i_data.name) === -1 ? names.push(i_data.name) : undefined;
      };
    }

    //Increse maximum by one, so highest element is below the max (otherwise it is not counted)
    max++;
    if (this.loop_days) {
      min = 0;
      max = this.loop_factor;
      if (this.timestampdata) {
        var UTCoffset = this.getUTCOffset();
        min = min + UTCoffset;
        max = max + UTCoffset;
      }
    }
    //Period between earliest and latest timestamp
    var diff;
    diff = max - min;
    //Period divided into num_sperations equal chunks
    var step = diff / this.num_seperations;
    //Define shape of outer ouput array
    let computed_d: { name: string, series: any[] }[] = [];

    //For each chunk
    for (var j = 0; j < this.num_seperations; j++) {
      //Array for counting occurence of "sensor_name" in time-chunk
      var counts = new Array(names.length);

      //Define shape of inner array
      let temp_series: { name: string, value: number }[] = [];

      //Depending on input type this either uses "timestamp" or "value" fields
      if (this.timestampdata) {
        //Count the occurences in this chunk
        for (var entry of somedata) {
          if ((this.loop_days) && ((min + j * step) <= ((entry.timestamp - UTCoffset) % this.loop_factor + UTCoffset) && ((entry.timestamp - UTCoffset) % this.loop_factor + UTCoffset) < (min + (j + 1) * step))) {
            counts[names.indexOf(entry.name)]++;
          }
          else if ((min + j * step) <= entry.timestamp && entry.timestamp < (min + (j + 1) * step)) {
            counts[names.indexOf(entry.name)]++;
          }
        }
      }
      else {
        //Count the occurences in this chunk
        for (var entry of somedata) {
          if ((this.loop_days) && ((min + j * step) <= (entry.value % this.loop_factor) && (entry.value % this.loop_factor) < (min + (j + 1) * step))) {
            counts[names.indexOf(entry.name)]++;
          }
          else if ((min + j * step) <= entry.value && entry.value < (min + (j + 1) * step)) {
            counts[names.indexOf(entry.name)]++;
          }
        }
      }


      //Fill inner array
      for (var count in counts) {
        temp_series.push({ name: names[count], value: counts[count] });
      }
      //Create String for time-chunk
      var step_name = Math.floor(min + j * step) + "-" + Math.floor(min + (j + 1) * step);
      if (this.prettyprint_timestamps) step_name = this.timeToString(min + j * step, diff) + "-" + this.timeToString(min + (j + 1) * step, diff);
      //Fill outer array
      computed_d.push({ name: step_name, series: temp_series });
    }

    return computed_d;
  };


  /**
   * Generates a Timestamp-String from a Unix timestamp
   * Leaves out the date, if @param difference is at most a day
   * @param timestamp A Unix timestamp
   * @param difference The difference between the lowest and highest timestamp in the given @param data
   */
  timeToString(timestamp, difference) {
    //Create a new JavaScript Date object based on the timestamp
    //multiplied by 1000 so that the argument is in milliseconds, not seconds.
    var date = new Date((timestamp) * 1000);

    //Hours part from the timestamp
    var hours = date.getHours();
    //Minutes part from the timestamp
    var minutes = "0" + date.getMinutes();
    var datum = date.getDate();
    //For unknown reasons Months start at index 0
    var month = date.getMonth() + 1;
    var year = date.getFullYear();

    //Removes dates if data covers less than a day
    if (difference > 86400) {
      return hours + ':' + minutes.substr(-2) + ' ' + datum + '/' + month + '/' + year;
    }
    else {
      // Will display time in 10:23 format
      return hours + ':' + minutes.substr(-2);
      //return timestamp;
    }
  };

  getUTCOffset() {
    var date = new Date(0);
    return date.getTimezoneOffset() * 60;
  }

  public ngOnInit(): void { }
}

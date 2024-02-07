import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { GemBulletChart as GemBulletChartTOP } from './gem-bullet-chart/gem-bullet-chart.component-gen';

@Component({
  selector: 'gem-bullet-chart',
  templateUrl: './gem-bullet-chart.component.html',
  styleUrls: ['./gem-bullet-chart.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GemBulletChart extends GemBulletChartTOP {

  constructor() { super(); }

  ngOnInit(): void { }

  topmargin: number = 20;
  bottommargin: number = 25;
  leftmargin: number = 25;
  rightmargin: number = 0;

  automargins: boolean = true;

  stepcolors: string[] = ["#8EBAE5", "#fdd48f", "#b8d698", "#e69679", "#BCB5D7", "#FFF59B", "#7DA4A7", "#89CCCF", "#CD8B87", "#A8859E"];

  /**
   * Default position of the arrow indicator relative to the position of the value
   */
  arrowposition: string = "top";

  /**
   * Font size of the title
   */
  fonstsize: number = 15;

  /**
   * Thickness of the threshold line
   */
  thresholdwidth: number = 3;

  /**
   * Default background color of the chart
   */
  bgcolor: string = "white";

  /**
   * Default bar color
   */
  brcolor: string = "#00549F";

  /**
   * Stores the length of the title for the adaptive left margin logic
   */
  titlelength: number = 1;

  /**
   * Coefficient used in the adaptive left margin logic. Recommended value is 7
   */
  coef: number = 7;

  /**
   * Contains information about the chart, which is passed to plotly.js, such as:
   * data, type of the chart, title, font sizes, colors etc.
   */
  _data = [
    {
      type: "indicator",
      mode: "number+gauge+delta",
      value: 0,
      delta: { reference: 0, position: this.arrowposition, increasing: { color: "#11750e" }, decreasing: { color: "#cc071e" } },
      title: {
        text: "",
        font: { size: this.fonstsize }
      },
      gauge: {
        shape: "bullet",
        axis: { range: [0, 0] },
        threshold: {
          line: { color: "", width: this.thresholdwidth },
          value: 0
        },
        bgcolor: this.bgcolor,
        steps: [],
        bar: { color: this.brcolor }
      }
    }
  ];

  /**
   * Contains information about the chart, which is passed to plotly.js, such as:
   * margins.
   */
  _layout = {
    margin: {
      r: this.rightmargin,
      t: this.topmargin,
      b: this.bottommargin,
      l: this.leftmargin
    }
  }

  /**
   * Sets the main value to be displayed and warns if empty.
   * Changes the color of the threshold line depending on the value.
   */
  @Input()
  public set value(value: number) {
    if (value != null) {
      this._data[0].value = value;
      if (this._data[0].value >= this._data[0].gauge.threshold.value) {
        this._data[0].gauge.threshold.line.color = "#11750e";
      }
      else {
        this._data[0].gauge.threshold.line.color = "#cc071e";
      }
    }
  }

  /**
   * Setter for a reference value (e.g. an old value, which the indicator
   * used to hold, or a certain goal), which the chart compares to the current value
   */
  @Input()
  public set reference(referencevalue: number) {
    this._data[0].delta.reference = referencevalue;
  }

  /**
   * Setter for the value of the threshold line.
   * Changes the color of the threshold line depending on the main value.
   */
  @Input()
  public set thresholdvalue(thresholdvalue: number) {
    this._data[0].gauge.threshold.value = thresholdvalue;
    if (this._data[0].value >= this._data[0].gauge.threshold.value) {
      this._data[0].gauge.threshold.line.color = "#11750e";
    }
    else {
      this._data[0].gauge.threshold.line.color = "#cc071e";
    }
  }

  /**
   * Setter for the title of the chart.
   * Formats the title in bold and adapts the size of the left margin,
   * if no margins were specified explicitly.
   */
  @Input()
  public set title(title: string) {
    if (title != null && title.length != 0) {
      this._data[0].title.text = "<b>" + title + "</b>";
      this.titlelength = title.length;
      if (this.automargins) {
        // Adaptive left margin logic
        this._layout.margin.l = this.titlelength * this.coef + this.leftmargin;
      }
    }
  }

  /**
   * Setter for the subtitle of the chart.
   * Formats the subtitle in a smaller, lighter colored font and adapts the size of the left margin,
   * if no margins were specified explicitly.
   */
  @Input()
  public set subtitle(subtitle: string) {
    if (subtitle != null && this._data[0].title.text.length > 0) {
      this._data[0].title.text += "</b><br><span style='color: gray; font-size:0.8em'>" + subtitle + "</span>";
      if (subtitle.length > this.titlelength) {
        this.titlelength = subtitle.length;
      }
      if (this.automargins) {
        // Adaptive left margin logic
        this._layout.margin.l = this.titlelength * this.coef + this.leftmargin;
      }
    }
  }

  /**
   * Setter for the range of the indicator
   */
  @Input()
  public set range(range: number[]) {
    this._data[0].gauge.axis.range = range;
  }

  /**
   * Setter for the background color of the chart
   */
  @Input()
  public set backgroundcolor(backgroundcolor: string) {
    if (backgroundcolor != null) {
      this._data[0].gauge.bgcolor = backgroundcolor;
    }
    else this._data[0].gauge.bgcolor = this.bgcolor;
  }

  /**
   * Setter for the different steps of the indicator
   * Configures each range's color, depending on the amount of ranges
   * @param steps An array consisting of numbers, representing the beginnings of each step's range.
   * E.g. input [0, 150, 210, 250, 300] would produce four steps: 0-150, 150-210, 210-250, 250-300.
   * If the entire range of the chart is 0-300, the input can further be reduced to [150, 210, 250].
   */
  @Input()
  public set steps(steps: number[]) {
    var modifiedsteps = steps;
    if (steps[0] != this._data[0].gauge.axis.range[0]) {
      modifiedsteps = [this._data[0].gauge.axis.range[0]];
      for (let index = 0; index < steps.length; index++) {
        modifiedsteps.push(steps[index]);
      }
    }
    if (modifiedsteps[modifiedsteps.length - 1] != this._data[0].gauge.axis.range[1]) {
      modifiedsteps.push(this._data[0].gauge.axis.range[1]);
    }
    for (let index = 0; index < modifiedsteps.length - 1; index++) {
      this._data[0].gauge.steps.push({});
      this._data[0].gauge.steps[index].range = [modifiedsteps[index], modifiedsteps[index + 1]];

    }

    var count = modifiedsteps.length - 1;

    /*
    for (let index = 0; index < steps.length; index++) {
      this._data[0].gauge.steps.push({});
      this._data[0].gauge.steps[index].range = steps[index];
    }
    if (this._data[0].gauge.steps[steps.length - 1].range[1] != this._data[0].gauge.axis.range[1]) {
      this._data[0].gauge.steps.push({});
      this._data[0].gauge.steps[steps.length].range = [this._data[0].gauge.steps[steps.length - 1].range[1], this._data[0].gauge.axis.range[1]];
      count++;
    }
    */

    if (count == 1) {
      this._data[0].gauge.steps[0].color = this.bgcolor;
    }
    else if (count == 2) {
      this._data[0].gauge.steps[0].color = "#e69679";
      this._data[0].gauge.steps[1].color = "#b8d698";
    }
    else if (count == 3) {
      this._data[0].gauge.steps[0].color = "#e69679";
      this._data[0].gauge.steps[1].color = "#fdd48f";
      this._data[0].gauge.steps[2].color = "#b8d698";
    }
    else if (count >= 4) {
      this._data[0].gauge.steps[0].color = "#e69679";
      this._data[0].gauge.steps[1].color = "#fdd48f";
      this._data[0].gauge.steps[2].color = "#FFF59B";
      this._data[0].gauge.steps[3].color = "#b8d698";
    }
    if (count >= 5) {
      this._data[0].gauge.steps[4].color = "#8EBAE5";
    }
    if (count >= 6) {
      this._data[0].gauge.steps[5].color = "#BCB5D7";
    }
    if (count >= 7) {
      this._data[0].gauge.steps[6].color = "#A8859E";
    }
    if (count > 7) {
      for (let index = 7; index < count; index++) {
        this._data[0].gauge.steps[index].color = this.stepcolors[index % 10];
      }
    }
  }

  /**
   * Setter for the color of the indicator bar
   */
  @Input()
  public set barcolor(barcolor: string) {
    if (barcolor != null) {
      this._data[0].gauge.bar.color = barcolor;
    }
    else this._data[0].gauge.bar.color = this.brcolor;
  }

  /**
   * Setter for the margins of the chart
   * If no input is recieved, default margins are used, and the left margin is adapted to the length of the title in order to fit it
   * @param margins is a number or an array containing of between one and four numbers (just like the CSS margin property):
   * 10 or [10] would set each margin to 10;
   * [10,20] would set the top and bottom margins to 10, but the left and right margins to 20;
   * [10,20,30] would set the top margin to 10, the left and right margins to 20, and the bottom one to 30.
   * [10,20,30,40] would set the top margin to 10, the right to 20, bottom to 30 and left to 40.
   */
  @Input()
  public set margins(margins: number[] | number) {
    if (margins != null) {
      this.automargins = false;
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
      this.automargins = true;
      this._layout.margin.t = this.topmargin;
      this._layout.margin.b = this.bottommargin;
      this._layout.margin.l = this.leftmargin;
      this._layout.margin.r = this.rightmargin;
      // Adaptive left margin logic
      this._layout.margin.l = this.titlelength * this.coef + this.leftmargin;
      console.log(this._layout.margin);
    }
  }
}
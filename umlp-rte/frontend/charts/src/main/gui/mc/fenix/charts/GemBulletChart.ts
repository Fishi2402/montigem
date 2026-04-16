/* (c) https://github.com/MontiCore/monticore */

import { ChangeDetectionStrategy, Component, Input, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { config, GemBulletChartComponent } from '../../../../../../build/gui2java2ts/mc/fenix/charts/GemBulletChartComponent';

@Component({
  standalone: true,
  imports: [CommonModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GemBulletChart extends GemBulletChartComponent implements AfterViewInit {
  @ViewChild('chartContainer') chartContainer!: ElementRef;

  private Plotly: any;
  private plotInitialized = false;

  constructor() { super(); }

  topmargin: number = 20;
  bottommargin: number = 25;
  leftmargin: number = 40;
  rightmargin: number = 0;

  automargins: boolean = true;
  stepcolors: string[] = ["#8EBAE5", "#fdd48f", "#b8d698", "#e69679", "#BCB5D7", "#FFF59B", "#7DA4A7", "#89CCCF", "#CD8B87", "#A8859E"];

  arrowposition: string = "top";
  fonstsize: number = 15;
  thresholdwidth: number = 3;
  bgcolor: string = "white";
  brcolor: string = "#00549F";
  titlelength: number = 1;

  private _rawTitle: string = "";
  private _rawSubtitle: string = "";

  _data: any[] = [
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

  _layout = {
    margin: {
      r: this.rightmargin,
      t: this.topmargin,
      b: this.bottommargin,
      l: this.leftmargin
    }
  };

  ngAfterViewInit(): void {
    if (this.chartContainer) {
      // Small timeout to ensure the container has its final dimensions
      setTimeout(() => {
        this.loadPlotlyAndCreatePlot();
      }, 100);
    }
  }

  private async loadPlotlyAndCreatePlot() {
    try {
      const plotlyModule = await import('plotly.js-dist-min/plotly.min.js');
      this.Plotly = plotlyModule.default || plotlyModule;
      this.createPlot();
    } catch (error) {
      console.error("Failed to load Plotly lazily:", error);
    }
  }

  private createPlot(): void {
    if (this.chartContainer && this._data && this._layout && this.Plotly) {
      this.Plotly.newPlot(this.chartContainer.nativeElement, this._data, this._layout, { responsive: true });
      this.plotInitialized = true;
    }
  }

  private updatePlot(): void {
    if (this.plotInitialized && this.chartContainer && this._data && this._layout && this.Plotly) {
      this.Plotly.react(this.chartContainer.nativeElement, this._data, this._layout);
    }
  }

  /** Helper method: Reconstructs the title string and calculates dynamic margin based on font size */
  private updateTitleText() {
    let combinedText = "";
    this.titlelength = 0;

    if (this._rawTitle) {
      combinedText = "<b>" + this._rawTitle + "</b>";
      this.titlelength = this._rawTitle.length;
    }
    if (this._rawSubtitle && this._rawTitle) {
      combinedText += "<br><span style='color: gray; font-size:0.8em'>" + this._rawSubtitle + "</span>";
      if (this._rawSubtitle.length > this.titlelength) {
        this.titlelength = this._rawSubtitle.length;
      }
    }
    this._data[0].title.text = combinedText;

    if (this.automargins) {
      const factor = 0.55;
      const dynamicTextMargin = this.titlelength * this.fonstsize * factor;

      this._layout.margin.l = dynamicTextMargin + this.leftmargin;
    }
  }

  @Input()
  public set value(value: number) {
    if (value != null) {
      this._data[0].value = value;
      this._data[0].gauge.threshold.line.color = (value >= this._data[0].gauge.threshold.value) ? "#11750e" : "#cc071e";
      this.updatePlot();
    }
  }

  @Input()
  public set reference(referencevalue: number) {
    this._data[0].delta.reference = referencevalue;
    this.updatePlot();
  }

  @Input()
  public set thresholdvalue(thresholdvalue: number) {
    this._data[0].gauge.threshold.value = thresholdvalue;
    this._data[0].gauge.threshold.line.color = (this._data[0].value >= thresholdvalue) ? "#11750e" : "#cc071e";
    this.updatePlot();
  }

  @Input()
  public set title(title: string) {
    if (title != null) {
      this._rawTitle = title;
      this.updateTitleText();
      this.updatePlot();
    }
  }

  @Input()
  public set subtitle(subtitle: string) {
    if (subtitle != null) {
      this._rawSubtitle = subtitle;
      this.updateTitleText();
      this.updatePlot();
    }
  }

  @Input()
  public set range(range: number[]) {
    this._data[0].gauge.axis.range = range;
    this.updatePlot();
  }

  @Input()
  public set backgroundcolor(backgroundcolor: string) {
    this._data[0].gauge.bgcolor = backgroundcolor != null ? backgroundcolor : this.bgcolor;
    this.updatePlot();
  }

  @Input()
  public set steps(steps: number[]) {
    if (!steps || steps.length === 0) return;

    this._data[0].gauge.steps = [];

    let modifiedsteps = [...steps];
    if (steps[0] != this._data[0].gauge.axis.range[0]) {
      modifiedsteps.unshift(this._data[0].gauge.axis.range[0]);
    }
    if (modifiedsteps[modifiedsteps.length - 1] != this._data[0].gauge.axis.range[1]) {
      modifiedsteps.push(this._data[0].gauge.axis.range[1]);
    }

    for (let index = 0; index < modifiedsteps.length - 1; index++) {
      this._data[0].gauge.steps.push({
        range: [modifiedsteps[index], modifiedsteps[index + 1]]
      });
    }

    let count = modifiedsteps.length - 1;

    if (count == 1) {
      this._data[0].gauge.steps[0].color = this.bgcolor;
    } else if (count == 2) {
      this._data[0].gauge.steps[0].color = "#e69679";
      this._data[0].gauge.steps[1].color = "#b8d698";
    } else if (count == 3) {
      this._data[0].gauge.steps[0].color = "#e69679";
      this._data[0].gauge.steps[1].color = "#fdd48f";
      this._data[0].gauge.steps[2].color = "#b8d698";
    } else if (count >= 4) {
      this._data[0].gauge.steps[0].color = "#e69679";
      this._data[0].gauge.steps[1].color = "#fdd48f";
      this._data[0].gauge.steps[2].color = "#FFF59B";
      this._data[0].gauge.steps[3].color = "#b8d698";
    }
    if (count >= 5) this._data[0].gauge.steps[4].color = "#8EBAE5";
    if (count >= 6) this._data[0].gauge.steps[5].color = "#BCB5D7";
    if (count >= 7) this._data[0].gauge.steps[6].color = "#A8859E";

    if (count > 7) {
      for (let index = 7; index < count; index++) {
        this._data[0].gauge.steps[index].color = this.stepcolors[index % 10];
      }
    }
    this.updatePlot();
  }

  @Input()
  public set barcolor(barcolor: string) {
    this._data[0].gauge.bar.color = barcolor != null ? barcolor : this.brcolor;
    this.updatePlot();
  }

  @Input()
  public set margins(margins: number[] | number) {
    if (margins != null) {
      this.automargins = false;
      if (typeof margins == "number") {
        this._layout.margin = { t: margins, b: margins, l: margins, r: margins };
      } else {
        if (margins.length == 1) {
          this._layout.margin = { t: margins[0], b: margins[0], l: margins[0], r: margins[0] };
        } else if (margins.length == 2) {
          this._layout.margin = { t: margins[0], b: margins[0], l: margins[1], r: margins[1] };
        } else if (margins.length == 3) {
          this._layout.margin = { t: margins[0], b: margins[2], l: margins[1], r: margins[1] };
        } else if (margins.length >= 4) {
          this._layout.margin = { t: margins[0], r: margins[1], b: margins[2], l: margins[3] };
        }
      }
    } else {
      this.automargins = true;
      this._layout.margin = { t: this.topmargin, b: this.bottommargin, l: this.leftmargin, r: this.rightmargin };
      this._layout.margin.l = this.titlelength * this.fonstsize * 0.55 + this.leftmargin;
    }
    this.updatePlot();
  }
}

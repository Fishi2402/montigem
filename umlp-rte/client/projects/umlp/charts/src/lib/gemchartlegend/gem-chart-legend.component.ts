import { Component, Input } from "@angular/core";
import { GemChartLegend as GemChartLegendTOP } from "./gem-chart-legend/gem-chart-legend.component-gen";

@Component({
  selector: "gem-chart-legend",
  templateUrl: "./gem-chart-legend.component.html",
  styleUrls: ["./gem-chart-legend.component.scss"],
})
export class GemChartLegend extends GemChartLegendTOP {
  /**contains the colors of the legend */
  public _colors: string[] = [];

  /**contains the labels of the legend */
  public _labels: string[] = [];

  /**contains the entries of the legend */
  public _entries: {label: string, color: string}[] = []

  /** variable that sets if the legend should be displayed */
  public _displayLegend: boolean;

  @Input()
  public set displayLegend(displayLegend: boolean) {
    this._displayLegend = displayLegend;
  }

  @Input()
  public set labels(labels: string[]) {
    this._labels = labels;
  }

  /** setter that processes the color data to be shown correctly by the legend. */
  @Input()
  public set colors(colors: string[]) {
    this._entries = [];

    for (let i = 0; i < this._labels.length; i++) {
      let legendEntry = {
        label: this._labels[i],
        color: colors[i],
      };
      this._entries.push(legendEntry);
    }
  }

  constructor() {
    super();
  }

  ngOnInit(): void { }
}

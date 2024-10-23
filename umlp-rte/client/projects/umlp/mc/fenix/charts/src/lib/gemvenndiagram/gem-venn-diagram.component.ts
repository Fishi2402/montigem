import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as venn from 'venn.js';
import * as d3 from 'd3';
import { autoColors } from "../colors";
import { GemVennDiagram as GemVennDiagramTOP } from './gem-venn-diagram/gem-venn-diagram.component-gen';
import {GemVennDiagramData} from "../gen/gemvenndiagramtypes/GemVennDiagramData";

@Component({
  selector: 'gem-venn-diagram',
  templateUrl: './gem-venn-diagram.component.html',
  styleUrls: ['./gem-venn-diagram.component.scss']
})

export class GemVennDiagram extends GemVennDiagramTOP {
  /**all labels of the entries in the chart*/
  public labels: string[];

  /**all color of the entries */
  public colors = [];

  /**contains the data for each entry */
  public _data: GemVennDiagramData;

  public _chart = venn.VennDiagram()

  /**labels for chart legend */
  public legendLabels: string[];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;

  /**variable that enables or disables the legend */
  public displayLegend = true;

  /**gives the colors from the constant autoColors */
  public getColors() {
    return autoColors;
  }

  @Input()
  public set data(data: GemVennDiagramData) {
    if (!data) {
      return;
    }
    const bgrColor = this.colorScheme.domain.map(x => x + "70");
    this.labels = data.getEntriesList().map(entry => entry.getLabel());
    this._data = data;

    this.legendLabels = this.labels.filter(entry => {
      return entry !== undefined;
    });
    this.legendColors = bgrColor;

    this.updateValues();
  }

  public colorScheme = {
    domain: this.getColors()
  };

  constructor() {
    super();
  }

  ngOnInit() {
    this.colors = this.getColors();
    this.updateValues();
  }

  public get sets() {
    return this._data;
  }

  private getColor(index: number) {
    return this.colors[index % this.colors.length];
  }

  private updateValues() {
    let div = d3.select("#venn");
    div.datum(this._data.getEntriesList()).call(this._chart);

    let tooltip = d3.select("body").append("div").attr("class", "tooltip");

    div.selectAll("g")
      .on("mouseover", (event, d) => {
        venn.sortAreas(div, d);

        tooltip.transition().duration(400).style("opacity", .9);
        tooltip.text(d.size + " publications");

        let selection = d3.select(event.currentTarget).transition("tooltip").duration(400);
        selection.select("path")
          .style("stroke-widht", 3)
          .style("fill-opacity", d.sets.length == 1 ? .4 : .1)
          .style("stroke-opacity", 1);
      })
      .on("mousemove", (event) => {
        tooltip.style("left", (event.pageX) + "px")
          .style("top", (event.pageY - 28) + "px");
      })
      .on("mouseout", (event, d) => {
        tooltip.transition().duration(400).style("opacity", 0);
        var selection = d3.select(event.currentTarget).transition("tooltip").duration(400);
        selection.select("path")
          .style("stroke-width", 0)
          .style("fill-opacity", d.sets.length == 1 ? .25 : .0)
          .style("stroke-opacity", 0);
      });
    d3.selectAll(".venn-circle path").style("fill", (event, d) => {
      return this.getColor(d);
    });
    d3.selectAll("#venn text").style("fill", (event, d) => {
      return this.getColor(d);
    });
    d3.selectAll("#venn .venn-circle").on("click", (event, d) => { console.log(d); this.emitClick(d); })
    d3.selectAll("#venn .venn-intersection").on("click", (event, d) => { console.log(d); this.emitClick(d); })
  }

  @Output() public onClick = new EventEmitter<string[]>();

  private emitClick(intersection: any) {
    this.onClick.emit(intersection.sets);
  }

  public setLegendPosition(legendPosition: string) {
    this.legendPosition = legendPosition;
  }

  /** changes container style depending on legend position */
  public changeContainer() {
    switch(this.legendPosition) {
      case 'left':
        return 'wholeChartWithLegendLeftContainer';
      case 'top':
        return 'wholeChartWithLegendTopContainer';
      case 'bottom':
        return 'wholeChartWithLegendBottomContainer';
      case 'right':
        return 'wholeChartWithLegendRightContainer';
      default:
        return 'wholeChartWithLegendRightContainer';
    }
  }

}

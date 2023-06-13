import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { Network, Options, DataView, DataSet } from 'vis-network/standalone';
import { GemNetworkData } from './gem-network-graph-types/gem-network-graph-types.types-gen';
import { GemNetworkGraph as GemNetworkGraphTOP } from './gem-network-graph/gem-network-graph.component-gen';

@Component({
  selector: 'gem-network-graph',
  templateUrl: './gem-network-graph.component.html',
  styleUrls: ['./gem-network-graph.component.scss']
})
export class GemNetworkGraph extends GemNetworkGraphTOP {
  /**contains the data for each entry */
  public _data: GemNetworkData = { nodes: [], edges: [] };

  /**labels for chart legend */
  public legendLabels: string[];

  /**colors for chart legend */
  public legendColors: string[] = [];

  /** defines the position of the legend */
  public legendPosition: string;
  
  /**variable that enables or disables the legend */
  public displayLegend = true;

  private _network = null;
  private nodesView;
  public _nodes;

  private _labelEqualsID = false;
  private _filter;

  constructor() {
    super();
  }

  ngOnInit() {
  }

  @Input()
  public set labelEqualsID(input: boolean) {
    this._labelEqualsID = input;
  }

  @Input()
  public set data(data: GemNetworkData){
    this._data.edges = data.edges;
    this._nodes = new DataSet();
    
    if (!this._labelEqualsID) {
      this._nodes.add(data.nodes);
    } else {
      for (let elem of data.nodes) {
        if (typeof elem.id === "string") {
          elem.label = elem.id;
        }
        this._nodes.add(elem);
      }
    }

    let nodeColors = data.nodes.map(entry => entry.color);
    const bgrColor = [...new Set(nodeColors)];

    this.legendColors = bgrColor;
    this.legendLabels = ["acylic", "cylic"]

    this.prepareData();
    this.redrawGraph();
  }

  @Input()
  public set filter(func: any) {
    if (typeof func === "function") {
      this._filter = func;
      this.prepareData();
      this.redrawGraph();
    }
  }

  @Input() filterEvents: Observable<void>;

  @Input()
  public set options(options: Options) {
    if (!!options) {
      this._options = options;
      this.redrawGraph();
    }
  }

  private _options: Options = {
    nodes: {
      shape: "circle",
      size: 30,
      font: {
        size: 32,
        color: "#ffffff",
      },
      borderWidth: 2,
    },
    edges: {
      width: 2,
    }
  }

  private prepareData() {
    if (!!this._nodes && this._nodes.length != 0) {
      if (this._filter) {
        this.nodesView = new DataView(this._nodes, { filter: this._filter });
        this.filterEvents.subscribe(() => {
          this.nodesView.refresh();
        });
        this._data.nodes = this.nodesView;
      } else {
        this._data.nodes = this._nodes;
      }
    }
  }

  private redrawGraph() {
    const container = document.getElementById("network");
    this._network = new Network(container, this._data, this._options);
    if (this.hasData()) {
      this._network.on("stabilizationProgress", (params) => {
        let networkheight = container.offsetHeight;
        let networkwidth = container.offsetWidth;
        document.getElementById("loadingBar").style.display = "inline";
        document.getElementById("outerBorder").style.marginLeft = (networkwidth / 2 - 300) + "px";
        document.getElementById("outerBorder").style.marginTop = "-" + (networkheight / 2 - 22) + "px";

        document.getElementById("loadingBar").style.opacity = "100";
        let maxWidth = 496;
        let minWidth = 20;
        let widthFactor = params.iterations / params.total;
        let width = Math.max(minWidth, maxWidth * widthFactor);

        document.getElementById("bar").style.width = width + "px";
        document.getElementById("text").innerText =
          Math.round(widthFactor * 100) + "%";
      });
      this._network.once("stabilizationIterationsDone", () => {
        document.getElementById("text").innerText = "100%";
        document.getElementById("bar").style.width = "496px";
        document.getElementById("loadingBar").style.opacity = "0";
        // really clean the dom element
        setTimeout(function () {
          document.getElementById("loadingBar").style.display = "none";
        }, 500);
      });
    }
  }

  public hasData(): boolean {
    return !!this._data.nodes && this._data.nodes.length != 0;
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
        return 'wholeChartWithLegendBottomContainer';
    }
  }

}

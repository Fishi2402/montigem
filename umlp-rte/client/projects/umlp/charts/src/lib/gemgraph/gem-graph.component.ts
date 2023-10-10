import {ChangeDetectionStrategy, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from "@angular/core";
import Sigma from "sigma";
import { MultiGraph } from "graphology";
import { Settings } from "sigma/settings";
import { SerializedGraph } from "graphology-types";
import FA2Layout from "graphology-layout-forceatlas2/worker";
import FA2LayoutSupervisor from "graphology-layout-forceatlas2/worker";
import forceAtlas2 from "graphology-layout-forceatlas2";
import circular from "graphology-layout/circular";
// import { DownloadOptions } from "../../panels/menu-item/menu-item.const"; TODO Exporter
// import { downloadChartAsPNG } from "./sigmajs.exporter";
import { CameraState } from "sigma/types";
import {NetworkData} from "./gem-graph-types";

@Component({
  selector: "gem-graph",
  templateUrl: "./gem-graph.component.html",
  styleUrls: ["./gem-graph.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GemGraphComponent implements OnInit, OnDestroy{
  @ViewChild("sigmaContainer")
  container: ElementRef<HTMLDivElement> | undefined; // Reference to the HTML container
  panelData: { graph?: SerializedGraph; cameraState?: CameraState } | undefined = undefined;
  private graph: MultiGraph | undefined; // Sigma instance to hold the graph
  private chartRenderer: Sigma | undefined; // Sigma instance to hold the graph
  private layout: FA2LayoutSupervisor | undefined;
  private iteration: number = 0;
  private maxIteration: number = 300;
  private _data : NetworkData;

  // TODO remove these hardcoded colors
  private colors: any = {
    JarArchive: "#8F71E3",
    Archive: "#8f71e3",
    Directory: "#88DBE4",
    Pkg: "#ff9d00",
    JavaSourceFile: "#95e000",
    FreeMarkerTemplate: "#5e87fc",
    Type: "#ff6d6d",
    MCGrammar: "#ff19f0",
  };

  public ngOnInit(): void {
    // console.log(this.panelData);

  }

  @Input()
  public set data(data: NetworkData){
    if (!data) {
      return;
    }
    this._data = data
    console.log(this._data)
    this.visualizeWithSigma()
  }

  public ngAfterViewInit() {
    this.visualizeWithSigma()
  }

  private visualizeWithSigma() {
    console.debug(this.container, "Wird aufgerufen")
    if (!this.container) return;
    const { width, height } = this.container.nativeElement.getBoundingClientRect();
    const area = width * height;
    // Using logarithm for scaling adjustments
    const logArea = Math.log10(area + 1); // Adding 1 to avoid log(0)

    const exponentialDecayWithFactor = (area: number): number => {
      const decayConstant = 0.59;
      const factor = 92.68;
      return factor * Math.exp(-decayConstant * area);
    };

    const INITIAL_NODE_SIZE = 50;
    const SCALE_FACTOR = exponentialDecayWithFactor(logArea);
    const settings: Partial<Settings> = {
      defaultNodeColor: "#ec5148",
      allowInvalidContainer: true,
      zoomToSizeRatioFunction: (ratio: number) => {
        return Math.sqrt(ratio) * SCALE_FACTOR;
      },
      // Adjust stage padding to match exactly the node size scaled with the scale factor (to avoid the graph being cut
      // off)
      stagePadding: INITIAL_NODE_SIZE / SCALE_FACTOR,
    };

    const categoryColorMap: any = {};

    let graphRawData: Partial<SerializedGraph> = {
      nodes: this._data.nodes.map((n) => {
        console.debug(n)
        categoryColorMap[n.id] = n.color;

        return {
          ...n,
          key: String(n.id),
          type: "square",
          attributes: {
            // forceLabel: true,
            size: n.mass? n.mass : INITIAL_NODE_SIZE,
            label: n.label,
            color: n.color,
            // "color": n.color
          },
        };
      }),
      edges: this._data.edges.map((edge) => {
        return {
          ...edge,
          source: String(edge.from),
          target: String(edge.to),
          attributes: {
            size: INITIAL_NODE_SIZE / 4,
            color: categoryColorMap[edge.from],
          },
        };
      }),
    };

    // Try to restore graph data
    if (this.panelData?.graph) {
      graphRawData = this.panelData.graph;
    }
    console.log("create graph!");

    this.graph = new MultiGraph();
    this.graph.import(graphRawData);

    if (!this.panelData) {
      circular.assign(this.graph);
    }

    // Create a new Sigma instance with the container element
    if(this.chartRenderer) {
      this.chartRenderer.kill();
      this.chartRenderer = undefined;
    }

    this.chartRenderer = new Sigma(this.graph, this.container.nativeElement, settings);
    // Try to restore the camera position
    if (this.panelData?.cameraState) {
      this.chartRenderer.getCamera().setState(this.panelData.cameraState);
    }
    this.chartRenderer.resize();

    const sensibleSettings = forceAtlas2.inferSettings(this.graph);

    if (!this.panelData) {
      this.layout = new FA2Layout(this.graph, {
        settings: sensibleSettings,
        // settings: {
        //   barnesHutOptimize: true,
        //   gravity: 125,
        //   edgeWeightInfluence: 1,
        //   scalingRatio: 1.5,
        //   outboundAttractionDistribution: true,
        //   slowDown: 2,
        // }
      });

      (this.layout as any).worker.addEventListener("message", () => {
        this.iteration++;
        this.panelData = {
          graph: this.graph?.export(),
          cameraState: this.chartRenderer?.getCamera().getState(),
        };
        if (this.iteration >= this.maxIteration) {
          this.stopLayouting();
        }
      });

      this.layout.start();
    }
  }

  /** Override the downloadAsPNGHook function to improve the quality of downloaded images. */
/*  override downloadAsPNGHook(): void {
    this.ngZone.runOutsideAngular((): void => {
      if (this.chartRenderer) downloadChartAsPNG(this.chartRenderer, this.getName());
    });
  }*/

  public ngOnDestroy(): void {
    // Proper cleanup to avoid "WARNING: Too many active WebGL contexts. Oldest context will be lost."
    this.stopLayouting();
    if (this.chartRenderer) {
      this.chartRenderer.kill();
      this.chartRenderer = undefined;
    }

    if (this.graph) {
      this.graph.clear();
      this.graph = undefined;
    }
  }

  private stopLayouting(): void {
    this.layout?.stop();
  }
}

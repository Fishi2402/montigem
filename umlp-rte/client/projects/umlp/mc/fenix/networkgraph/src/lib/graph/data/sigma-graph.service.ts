import { Injectable, NgZone, OnDestroy } from "@angular/core";
import Graph from "graphology";
import { AnimateZoomOptions, EdgeAttributes, NodeAttributes } from "../types/graph.types";
import Sigma from "sigma/sigma";
import { GraphSettingsService } from "../services/graph-settings.service";
import { Attributes } from "graphology-types";
import { CameraState } from "sigma/types";
import { AnimateOptions } from "sigma/utils/animate";
import { Settings } from "sigma/settings";
import { Camera } from "sigma";
import { TypeCheckerUtil } from "../../shared/utils/type-checker.util";

/**
 * SigmaGraphService manages the Sigma graph renderer and associated graph features. It provides methods to create a
 * chart and provides an API to frequently used graph methods. For more specific methods, you can access the Sigma
 * renderer directly.
 *
 * @template N - Type of the Node Attributes
 * @template E - Type of the Edge Attributes
 * @template G - Type of the Graph Attributes
 */
@Injectable()
export class SigmaGraphService<N extends NodeAttributes, E extends EdgeAttributes, G extends Attributes>
  implements OnDestroy
{
  constructor(
    private graphSettings: GraphSettingsService,
    private ngZone: NgZone
  ) {}

  /** Store the Sigma Renderer instance */
  private _renderer: Sigma<Graph<N, E, G>> | null = null;

  /**
   * Getter for the Sigma renderer.
   *
   * @returns {Sigma<Graph<N, E, G>> | null} - The Sigma renderer instance or null.
   */
  public get renderer(): Sigma<Graph<N, E, G>> | null {
    return this._renderer;
  }

  /**
   * Getter for the graph instance.
   *
   * @returns {Graph<N, E, G> | null} - The graph instance or null.
   */
  public get graph(): Graph<N, E, G> | null {
    return this.renderer?.getGraph() ?? null;
  }

  /**
   * Getter for the camera.
   *
   * @returns {Camera | null} - The camera instance or null.
   */
  public get camera(): Camera | null {
    return this.renderer?.getCamera() ?? null;
  }

  /** Cleanup the graph on destroy. */
  public ngOnDestroy(): void {
    this.graph?.clear();

    // Proper cleanup to properly delete WebGL context.
    if (this.renderer) {
      this.renderer.clear();
      this.renderer.kill();
      // Free up all the context (see https://github.com/jacomyal/sigma.js/issues/1386)
      Object.values((this.renderer as any).webGLContexts as WebGLRenderingContext[]).forEach(
        (context: WebGLRenderingContext) => {
          context.getExtension("WEBGL_lose_context")?.loseContext();
        }
      );
      this._renderer = null;
    }
  }

  /**
   * Resets the zoom level of the camera.
   *
   * @param {Partial<AnimateOptions>} options - Optional animation settings.
   */
  public resetZoom(options?: Partial<AnimateOptions>): void {
    this._renderer?.getCamera().animatedReset({
      duration: this.graphSettings.getSetting("zoomAnimationDuration"),
      ...options,
    });
  }

  /** Resizes the graph rendering area. */
  public resize(): void {
    this.renderer?.resize();
    this.refresh();
  }

  /** Forces a simple redraw of the graph without re-initializing. */
  public refresh(): void {
    this.syncSettings();
    this.renderer?.refresh();
  }

  /**
   * Animates the camera to a new state.
   *
   * @param {Partial<CameraState>} state - New camera state.
   * @param {Partial<AnimateOptions>} options - Animation options.
   * @param {() => void} callback - Callback function to execute after animation.
   */
  public animateCamera(state: Partial<CameraState>, options?: Partial<AnimateOptions>, callback?: () => void): void {
    this.camera?.animate(
      {
        ratio: 0.1,
        ...state,
      },
      {
        duration: this.graphSettings.getSetting("zoomAnimationDuration"),
        ...options,
      },
      callback
    );
  }

  /**
   * Animates zooming in on the graph.
   *
   * @param {AnimateZoomOptions} options - Zoom options.
   */
  public animatedZoom(options?: AnimateZoomOptions): void {
    this.camera?.animatedZoom({
      duration: this.graphSettings.getSetting("zoomAnimationDuration"),
      ...(typeof options === "object" ? options : {}),
      factor: TypeCheckerUtil.isNumber(options)
        ? options
        : options?.factor ?? this.graphSettings.getSetting("zoomFactor"),
    });
  }

  /**
   * Animates zooming out on the graph.
   *
   * @param {AnimateZoomOptions} options - Zoom options.
   */
  public animatedUnzoom(options?: AnimateZoomOptions): void {
    this.camera?.animatedUnzoom({
      factor: TypeCheckerUtil.isNumber(options)
        ? options
        : options?.factor ?? this.graphSettings.getSetting("zoomFactor"),
      duration: this.graphSettings.getSetting("zoomAnimationDuration"),
      ...(typeof options === "object" ? options : {}),
    });
  }

  /**
   * Resets the camera with animation.
   *
   * @param {Partial<AnimateOptions>} options - Optional animation settings.
   */
  public animatedReset(options?: Partial<AnimateOptions>): void {
    this.camera?.animatedReset(options);
  }

  /** Syncs settings between Sigma and the GraphSettingsService. */
  public syncSettings(): void {
    if (!this.renderer) return;
    const sigmaSettings: Settings = this.renderer?.getSettings();
    for (const [key, value] of Object.entries(this.graphSettings.getSettings())) {
      if (key in sigmaSettings && sigmaSettings[key as keyof Settings] !== value) {
        this.renderer?.setSetting(key as keyof Settings, value);
      }
    }
  }

  /**
   * Sets the graph for the renderer.
   *
   * @param {Graph<N, E, G>} graph - The graph instance.
   * @returns {Graph<N, E, G> | null} - The set graph or null.
   */
  public setGraph(graph: Graph<N, E, G>): Graph<N, E, G> | null {
    this.syncSettings();
    this.renderer?.setGraph(graph);
    return this.graph;
  }

  /**
   * Creates a new Sigma renderer.
   *
   * @param {Graph<N, E, G>} graph - The graph instance.
   * @param {HTMLDivElement} container - The container element for the renderer.
   * @param {Partial<Settings>} settings - Settings for the Sigma renderer.
   * @returns {Sigma<Graph<N, E, G>> | null} - The created Sigma renderer or null.
   */
  public createRenderer(
    graph: Graph<N, E, G>,
    container: HTMLDivElement,
    settings: Partial<Settings>
  ): Sigma<Graph<N, E, G>> | null {
    return this.ngZone.runOutsideAngular(() => {
      this._renderer = new Sigma(graph, container, settings);
      return this.renderer;
    });
  }

  /**
   * Updates the camera state.
   *
   * @param {Partial<CameraState>} cameraState - New camera state.
   */
  public updateCameraState(cameraState: Partial<CameraState>): void {
    this.camera?.setState(cameraState);
  }
}

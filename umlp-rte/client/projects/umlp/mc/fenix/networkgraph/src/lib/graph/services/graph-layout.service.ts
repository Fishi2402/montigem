import {EventEmitter, Injectable, NgZone, OnDestroy} from "@angular/core";
import { COMPLEX_LAYOUT_MAPPING, INITIAL_LAYOUT_MAPPING, Layout } from "../consts/graph.const";
import { ComplexGraphLayoutConfiguration, InitialGraphLayoutConfiguration } from "../types/graph-layout.types";
import { BehaviorSubject, Observable } from "rxjs";
import { SigmaGraphService } from "../data/sigma-graph.service";
import { EdgeAttributes, NodeAttributes } from "../types/graph.types";
import { Attributes } from "graphology-types";

/**
 * GraphLayoutService handles the layouting process of graphs. It allows for initial layouts as well as complex layouts
 * based on various algorithms. The service also provides mechanisms to start, stop, and continue the layouting process.
 * Observable properties are exposed to track the layout's progress.
 */
@Injectable()
export class GraphLayoutService<N extends NodeAttributes, E extends EdgeAttributes, G extends Attributes>
  implements OnDestroy
{
  /** The Current layout algorithm being used */
  private currentLayout: Layout | null = null;

  /** The Current count of iterations for the layouting process */
  private _iterationCount: BehaviorSubject<number> = new BehaviorSubject(0);

  /** Observable for layout iteration count */
  public iterationCount$: Observable<number> = this._iterationCount.asObservable();

  /** Event once the layout service has been stopped */
  public finishedLayouting: EventEmitter<any> = new EventEmitter<any>();

  /** The Starting time for layouting */
  private elapsedLayoutTime: number = 0;

  /** The Maximum time in milliseconds for layouting */
  private maxLayoutTime: number | null = null;

  /** The Maximum count of iterations allowed for the layouting process */
  private maxIterations: number | null = 100;

  /** The ID for the animation frame used in layouting */
  private frameID: number | null = null;

  /** Indicates If the layouting process has been stopped */
  private isLayoutStopped: boolean = false;

  /** Stores The last used complex layout configuration */
  private lastComplexLayoutConfig: ComplexGraphLayoutConfiguration | null = null;

  constructor(
    private ngZone: NgZone,
    private graph: SigmaGraphService<N, E, G>
  ) {}

  /**
   * Get the current layouting status.
   *
   * @returns {boolean} - True if the layouting process is running, otherwise false.
   */
  public get isRunning(): boolean {
    return !this.isLayoutStopped;
  }

  /**
   * Get the current iteration count.
   *
   * @returns {number} - The current iteration count.
   */
  public getIterationCount(): number {
    return this._iterationCount.value;
  }

  /**
   * Assign layout based on initial configuration.
   *
   * @param {InitialGraphLayoutConfiguration | null} initialLayout - The initial layout settings.
   */
  public applyInitialLayout(initialLayout: InitialGraphLayoutConfiguration | null): void {
    if (initialLayout && this.graph.graph) {
      INITIAL_LAYOUT_MAPPING[initialLayout.mode](this.graph.graph, initialLayout.parameters ?? {});
    }
  }

  /**
   * Starts the layouting process based on the provided configuration. Resets the service to its initial state before
   * starting the new layouting process.
   *
   * @param {ComplexGraphLayoutConfiguration | null} layoutConfiguration - The complex layout settings
   */
  public startLayouting(layoutConfiguration: ComplexGraphLayoutConfiguration | null): void {
    this.ngZone.runOutsideAngular(() => {
      this.reset();
      // Store the last used layout configuration
      this.lastComplexLayoutConfig = layoutConfiguration;

      if (layoutConfiguration && this.graph.graph) {
        // Update the maxIterations and maxLayoutTime when the settings have been provided
        if (layoutConfiguration.maxIterations != null || layoutConfiguration.maxLayoutTime != null) {
          this.maxIterations = layoutConfiguration.maxIterations ?? null;
          this.maxLayoutTime = layoutConfiguration.maxLayoutTime ?? null;
        }

        // Call the proper layout constructor
        this.currentLayout = COMPLEX_LAYOUT_MAPPING[layoutConfiguration.mode](
          this.graph.graph,
          layoutConfiguration.parameters ?? {}
        );

        // Subscribe to Layout Updates and start time measurement
        this.subscribeToLayoutUpdates();
        this.elapsedLayoutTime = Date.now();

        // Start the layouting process
        this.currentLayout?.start();
      } else {
        // Stop layouting if no configuration or no graph has been passed
        this.stopLayouting();
      }
    });
  }

  /** Continue layouting with the last configuration. */
  public continueLayout(): void {
    this.lastComplexLayoutConfig ? this.startLayouting(this.lastComplexLayoutConfig) : this.stopLayouting();
  }

  /** Cleanup logic for destroying the service. */
  public ngOnDestroy(): void {
    this.reset();
  }

  /** Stop the layouting process. */
  public stopLayouting(): void {
    this.isLayoutStopped = true;
    this.currentLayout?.stop();
    this.currentLayout?.worker?.removeEventListener("message", this.processIteration.bind(this));

    if (this.frameID !== null) {
      window.cancelAnimationFrame(this.frameID);
      this.frameID = null;
    }

    this.finishedLayouting.emit()
  }

  /** Reset the service to its initial state. */
  public reset(): void {
    this.stopLayouting();
    this.currentLayout?.kill();
    this._iterationCount.next(0);
    this.isLayoutStopped = false;
    this.elapsedLayoutTime = 0;
  }

  /** Listen to layout worker and update after each iteration. */
  private subscribeToLayoutUpdates(): void {
    if (!this.currentLayout) return;
    this.currentLayout.worker ? this.monitorWorker() : this.monitorAnimationFrame();
  }

  /** Monitor layout iterations using Web Worker. */
  private monitorWorker(): void {
    this.currentLayout?.worker?.addEventListener("message", this.processIteration.bind(this));
  }

  /** Monitor layout iterations using requestAnimationFrame. */
  private monitorAnimationFrame(): void {
    this.frameID = window.requestAnimationFrame((): void => {
      if (this.processIteration()) return;
      this.monitorAnimationFrame();
    });
  }

  /**
   * Increments iteration counter and checks if layout process should stop.
   *
   * @returns {boolean} - True if layout should stop, otherwise false.
   */
  private processIteration(): boolean {
    this.ngZone.run(() => this._iterationCount.next(this.getIterationCount() + 1));
    const elapsedTime: number = Date.now() - this.elapsedLayoutTime;

    /*
     Check whether the layout has been due to:
       - external stop / layout finished
       - maximum number of iterations has been reached
       - maximum time has been reached
     */
    if (
      this.isLayoutStopped ||
      !this.currentLayout?.isRunning() ||
      (this.maxIterations != null && this.getIterationCount() >= this.maxIterations) ||
      (this.maxLayoutTime != null && elapsedTime >= this.maxLayoutTime)
    ) {
      this.stopLayouting();
      return true;
    }
    return false;
  }
}

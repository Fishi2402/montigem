import { Injectable } from "@angular/core";
import { GraphSettings } from "../types/graph-settings.types";
import { MergeUtil } from "../shared/utils/merge.util";
import { getDefaultGraphSettings } from "../consts/graph-settings.const";

/** Service for managing graph settings. */
@Injectable()
export class GraphSettingsService {
  /** Stores the settings for the graph. */
  private graphSettings: GraphSettings;

  /** Stores the size of the largest node. */
  private largestNodeSize: number;

  /** Constructor initializes the graph settings with default and custom values. */
  constructor() {
    this.graphSettings = getDefaultGraphSettings();
    this.largestNodeSize = this.getSetting("defaultNodeSize");
  }

  /**
   * @param {Partial<GraphSettings>} newSettings - The settings to be updated.
   * @returns {boolean} - Returns true if settings have been updated; otherwise false.
   */
  public updateSettings(newSettings: Partial<GraphSettings>): boolean {
    let isSettingsChanged: boolean = false;

    for (const [key, value] of Object.entries(newSettings)) {
      if (key in this.graphSettings) {
        const hasChanged: boolean = this.updateSetting(key as keyof GraphSettings, value);
        isSettingsChanged ||= hasChanged;
      }
    }

    // Enforce the rendering of edge labels when we want to enforce the visibility of all edge labels
    if (this.graphSettings.forceEdgeLabels) this.graphSettings.renderEdgeLabels = true;

    return isSettingsChanged;
  }

  /**
   * Updates a single graph setting
   *
   * @template K - The type of the key of the graph setting to update
   * @param {K} key - The key of the setting to be updated.
   * @param {GraphSettings[K]} value - The value of the setting to be updated.
   * @returns {boolean} - Returns true if settings have been updated; otherwise false.
   */
  public updateSetting<K extends keyof GraphSettings>(key: K, value: GraphSettings[K]): boolean {
    if (this.graphSettings[key] === value) return false;
    this.graphSettings[key] = MergeUtil.deepMerge(this.graphSettings[key], value);
    return true;
  }

  /**
   * Returns the current graph settings.
   *
   * @returns {GraphSettings} - Current graph settings.
   */
  public getSettings(): GraphSettings {
    return { ...this.graphSettings };
  }

  /**
   * Returns the setting associated with the given key.
   *
   * @template K - The type of the key.
   * @param {keyof GraphSettings} settingKey - The key for the setting to retrieve.
   * @returns {GraphSettings[K]} - Returns the value associated with the given key.
   */
  public getSetting<K extends keyof GraphSettings>(settingKey: K): GraphSettings[K] {
    return this.graphSettings[settingKey];
  }

  /**
   * Computes scaling values based on the area of the container.
   *
   * @param {HTMLDivElement} container - The HTML container element.
   */
  public computeAreaDependentScaling(container: HTMLDivElement): void {
    if (!this.getSetting("areaDependentScaling")) return;

    const { width, height } = container.getBoundingClientRect();
    const area: number = width * height;
    const logArea: number = Math.log10(area + 1);

    const scaleFactor: number = this.computeScaleFactor(logArea);

    this.updateSettings({
      zoomToSizeRatioFunction: (ratio: number) => {
        return Math.sqrt(ratio) * scaleFactor;
      },
    });
  }

  /** Computes and updates the padding for the stage. */
  public computeStagePadding(): void {
    this.updateSettings({
      stagePadding: this.largestNodeSize / this.getSetting("zoomToSizeRatioFunction")(1),
    });
  }

  /**
   * Sets the size of the largest node in the graph.
   *
   * @param {number} nodeSize - The size of the largest node.
   */
  public setLargestNodeSize(nodeSize: number): void {
    this.largestNodeSize = nodeSize;
  }

  /**
   * Computes the scaling factor based on the logarithmic area of the container.
   *
   * @param {number} logArea - Logarithmic area of the container.
   * @returns {number} - The computed scaling factor.
   */
  private computeScaleFactor(logArea: number): number {
    const DECAY_CONSTANT: number = 0.59;
    const FACTOR: number = 92.68;
    return FACTOR * Math.exp(-DECAY_CONSTANT * logArea);
  }
}

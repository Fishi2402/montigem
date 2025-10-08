import {
  EdgeAttributes,
  Internal_GraphEdge,
  Internal_GraphNode,
  NodeAttributes,
  RawEdgeAttributes,
  RawNodeAttributes,
} from "../types/graph.types";
import { inject, Injectable } from "@angular/core";
import { GraphSettingsService } from "../services/graph-settings.service";
import { PartialButFor } from "sigma/types";
import { Attributes } from "graphology-types";
import { GraphDataService } from "../data/graph-data.service";

@Injectable()
export abstract class ProcessorServiceTemplate<
  N extends NodeAttributes,
  E extends EdgeAttributes,
  G extends Attributes,
> {
  /** Graph settings service */
  protected graphSettings: GraphSettingsService = inject(GraphSettingsService);

  /** Graph data service */
  protected data: GraphDataService<N, E, G> = inject(GraphDataService);

  /** Default name to use in case of an unknown / unspecified group */
  protected unknownGroupName: string = this.data.getUnknownGroupName();

  /**
   * Retrieves a specific attribute of an object.
   *
   * @template T - The type of the key we want to retrieve the attribute from.
   * @template U - The type of the data to retrieve the attribute from.
   * @param {U} data - The object to get the attribute from.
   * @param {T[]} attributes - The attribute to try to get. The first non-null attribute will be used and the value will
   *   be returned accordingly.
   * @returns {U extends Internal_GraphNode ? Internal_GraphNode[T] : Internal_GraphEdge[T]} - The value of the specified attribute.
   */
  protected static getAttribute<T extends keyof Internal_GraphNode | keyof Internal_GraphEdge, U extends Internal_GraphNode | Internal_GraphEdge>(
    data: U,
    ...attributes: T[]
  ): U extends Internal_GraphNode ? Internal_GraphNode[T] : Internal_GraphEdge[T] {
    for (const attribute of attributes) {
      const value: (RawNodeAttributes | RawEdgeAttributes)[T] = data?.[attribute] ?? data.attributes?.[attribute];
      if (value != null) return value;
    }
    return undefined;
  }

  /**
   * Converts the given value to a string representation if it's defined; returns undefined otherwise.
   *
   * @param {unknown} value - The value to convert to string.
   * @returns {string | undefined} The string representation of the value, or undefined.
   */
  protected static toStringIfDefined(value: unknown): string | undefined {
    return value == null ? undefined : String(value);
  }

  /**
   * Generates a unique identifier for the given graph entity (either a node or an edge). It tries to use specified
   * attributes of the entity for the identifier, and falls back to a counter-based identifier if necessary.
   *
   * @param {number} counter - The current counter value to ensure uniqueness.
   * @param {Set<string>} keySet - Set of existing unique identifiers.
   * @param {Internal_GraphNode | Internal_GraphEdge} entity - The entity for which to generate the identifier.
   * @param {string} counterPrefix - Prefix for the counter-based identifier.
   * @param {string[]} [attributes] - Array of attributes to consider for generating the identifier.
   * @returns {[string, number]} - A tuple containing the unique identifier and the updated counter value.
   */
  protected static generateIdentifier(
    counter: number,
    keySet: Set<string>,
    entity: Internal_GraphNode | Internal_GraphEdge,
    counterPrefix: string,
    attributes: string[] = ["key", "id"]
  ): [string, number] {
    let identifier: string = String(ProcessorServiceTemplate.getAttribute(entity, ...attributes) ?? "");

    if (identifier === "") {
      do {
        identifier = `${counterPrefix}_auto_id_${counter++}`;
      } while (keySet.has(identifier));
    }

    return [identifier, counter];
  }

  /** Resets the internal state to its initial configuration. */
  public abstract reset(): void;

  /**
   * Checks whether the provided group name corresponds to an unknown group.
   *
   * @param {string} group - The group name to check.
   * @returns {boolean} - Whether the group is unknown or not.
   */
  protected isGroupUnknown(group: string): boolean {
    return group === this.unknownGroupName;
  }

  /**
   * Extracts common properties from a given graph entity (either a node or an edge), and returns them in a structured
   * format.
   *
   * @param {Internal_GraphNode | Internal_GraphEdge} data - The graph entity to analyze.
   * @param {string} fallbackLabel - The key that is used as a fallback label if none was provided
   * @returns {PartialButFor<N | E, "hidden" | "tooltip" | "label">} A structured representation of the common
   *   properties of the graph entity.
   */
  protected getCommonProperties(
    data: Internal_GraphNode | Internal_GraphEdge,
    fallbackLabel: string
  ): PartialButFor<N | E, "hidden" | "tooltip" | "label"> {
    return {
      hidden: ProcessorServiceTemplate.getAttribute(data, "hidden") || false,
      tooltip: ProcessorServiceTemplate.toStringIfDefined(
        ProcessorServiceTemplate.getAttribute(data, "tooltip", "hover", "hoverLabel")
      ),
      label:
        ProcessorServiceTemplate.toStringIfDefined(ProcessorServiceTemplate.getAttribute(data, "label", "name")) ??
        fallbackLabel,
    } as N | E;
  }
}

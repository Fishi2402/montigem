import { HEX } from "../../shared/utils/color.util";

export type SortedFilterGroupInformation = {
  /** The unique identifier for the group */
  groupKey: string;

  /** The number of items within the group */
  count: number;

  /** The width of the group's visual representation */
  width: number;

  /** The color associated with the group, represented as a HEX code */
  groupColor: HEX;
};

export type GroupInformation = {
  /** The number of items within the group */
  count: number;

  /** The color associated with the group, represented as a HEX code */
  groupColor: HEX;
};

export type SearchInformation = {
  /** An optional label that can be displayed in search results */
  searchLabel?: string;

  /** An optional group to which the node belongs */
  group?: string;

  /** An optional color associated with the node's group, represented as a HEX code */
  groupColor?: HEX;

  /** The unique identifier for the node */
  nodeKey: string;
};

/** Utility type that maps the group identifier to the number of total nodes in the group */
export type GroupInformationMap = Map<string, GroupInformation>;

/** An array of SearchInformation objects */
export type SearchInformationArray = SearchInformation[];

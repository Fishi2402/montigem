/* (c) https://github.com/MontiCore/monticore */

import { TemplateRef } from "@angular/core";

export class Group {
  level: number = 0;
  parent: Group;
  expanded: boolean = true;
  get visible(): boolean {
    return !this.parent || (this.parent.visible && this.parent.expanded);
  }
  props: { [key: string]: any } = {};
  extension: any;
  summary: { [key: string]: any } = {};
  style: { [key: string]: any };
  title?: string;
  gemSelected: boolean;
}

export interface GroupBy {
  initial: string;
  isGroupBy: boolean;
}

export enum SortDirection {
  ASCENDING = "asc",
  DESCENDING = "desc",
  NONE = "none",
}

export enum TableSelectionType {
  SINGLE = "single",
  MULTI = "multi",
  NONE = "none",
}

export enum TableSelectionStatus {
  ALL = "all",
  SOME = "some",
  NONE = "none",
}

export enum ColumnTypes {
  STRING,
  TIME,
  DATE,
}

export interface ComaparativeSigns {
  value: string;
  viewValue: string;
}

export interface FilterValue {
  column: GemTableColumn;
  firstValue: any;
  secondValue: any;
  firstOption: any;
  secondOption: any;
  secondRangeFirstFilter: any;
  secondRangeSecondFilter: any;
}

export const EMPTY_ROW_ID = 0;

export type BatchModeTypes = "single" | "batch";

export type TableRowHeight<T> = ((row: T) => number) | number | "auto";

export type TableMessages = {
  emptyMessage?: string;
  totalMessage?: string;
  selectedMessage?: string;
};

export type InvalidCellDescription = {
  displayValue: any;
  actualValue: any;
  invalid?: boolean;
  error?: string;
};

/**
 * Column Type
 * @type {object}
 */
export interface FlatTableColumn {
  id?: string;

  /**
   * Determines if column is checkbox
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  checkboxable?: boolean;

  /**
   * Determines if the column is frozen to the left
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  frozenLeft?: boolean;

  /**
   * Determines if the column is frozen to the right
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  frozenRight?: boolean;

  /**
   * The grow factor relative to other columns. Same as the flex-grow
   * API from http =//www.w3.org/TR/css3-flexbox/. Basically;
   * take any available extra width and distribute it proportionally
   * according to all columns' flexGrow values.
   *
   * @type {number}
   * @memberOf TableColumn
   */
  flexGrow?: number;

  /**
   * Min width of the column
   *
   * @type {number}
   * @memberOf TableColumn
   */
  minWidth?: number;

  /**
   * Max width of the column
   *
   * @type {number}
   * @memberOf TableColumn
   */
  maxWidth?: number;

  /**
   * The default width of the column, in pixels
   *
   * @type {number}
   * @memberOf TableColumn
   */
  width?: string;

  /**
   * Can the column be resized
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  resizable?: boolean;

  sortLevel?: number;

  displayValue?: (arg: any, row?: any, prop?: string) => any;

  type?: ColumnTypes;

  filterByModelValue?: boolean;

  /**
   * Can the column be sorted
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  sortable?: boolean;

  /**
   * Can the column be re-arranged by dragging
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  draggable?: boolean;

  /**
   * Whether the column can automatically resize to fill space in the table.
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  canAutoResize?: boolean;

  /**
   * Column name or label
   *
   * @type {string}
   * @memberOf TableColumn
   */
  name: string;

  /**
   * Property to bind to the row. Example:
   *
   * `someField` or `some.field.nested`, 0 (numeric)
   *
   * If left blank, will use the name as camel case conversion
   *
   * @type {string}
   * @memberOf TableColumn
   */
  prop: string;

  /**
   * Cell template ref
   *
   * @type {*}
   * @memberOf TableColumn
   */
  cellTemplate?: any;

  /**
   * Header template ref
   *
   * @type {*}
   * @memberOf TableColumn
   */
  headerTemplate?: any;

  /**
   * CSS Classes for the cell
   *
   *
   * @memberOf TableColumn
   */
  cellClass?: string | ((data: any) => string | any);

  /**
   * CSS classes for the header
   *
   *
   * @memberOf TableColumn
   */
  headerClass?: string | ((data: any) => string | any);

  /**
   * Header checkbox enabled
   *
   * @type {boolean}
   * @memberOf TableColumn
   */
  headerCheckboxable?: boolean;

  format?: string;

  detailsColumn?: boolean;

  tooltip?: { disabled?: boolean };

  summarize?: { function?: (arr: any[]) => any; extended?: boolean };

  sort?: { comparator: (a: any, b: any) => number; extended?: boolean };

  hidden?: boolean;

  excludeFromGrouping?: boolean;

  uneditable?: boolean;

  groupHeaderLimit?: number;

  editInView?: boolean;

  groupings?: {
    criterion: (arg: any) => any;
    style?: { [key: string]: string };
  }[];

  editTemplate?: TemplateRef<any>;

  displayTemplate?: TemplateRef<any>;

  footer?: TableFooter;

  isFiltered?: boolean;
}

export interface NestedTableColumn {
  [key: string]: NestedTableColumn | FlatTableColumn[];
}

export type GemTableColumn = FlatTableColumn;

export function instanceOfFlatTableColumn(
  object: any
): object is FlatTableColumn {
  return "name" in object && typeof object.name === "string";
}

export interface TableFooter {
  template?: TemplateRef<any>;

  value?: any;
}

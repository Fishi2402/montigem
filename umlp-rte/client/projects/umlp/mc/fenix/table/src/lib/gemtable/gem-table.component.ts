/* (c) https://github.com/MontiCore/monticore */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Directive,
  EventEmitter,
  HostListener,
  Input,
  Output,
  TemplateRef,
  ViewChild
} from "@angular/core";
import { MatCheckboxChange } from "@angular/material/checkbox";
import {
  MatSlideToggleChange
} from "@angular/material/slide-toggle";
import { MatTable } from "@angular/material/table";
import {
  EMPTY_ROW_ID,
  FilterValue,
  Group,
  InvalidCellDescription,
  SortDirection,
  GemTableColumn,
  TableSelectionStatus,
  TableSelectionType
} from "./gem-table.type";
import { getTableColumnId, getTableRowProp } from "./gem-table.util";

import {
  dateAsString,
  DateTimeFormat,
  stringAsDate
} from "./date-time.transformation";

import {
  ContextMenuItem,
  GemContextMenuComponent
} from "../gemcontextmenu/gem-context-menu.component";
import { Utils } from "./utils";

@Component({
  selector: "gem-table",
  templateUrl: "./gem-table.component.html",
  styleUrls: ["./gem-table.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GemTableComponent<
  T extends {
    gemId: number;
    gemSelected?: boolean;
  }
> {
  @ViewChild("table")
  table: MatTable<T>;

  /**
   * Defines a style function which should be applied to every cell
   *
   * @type {any} style object
   */
  @Input()
  public cellStyle: (column: GemTableColumn, row: T) => any = () => {
    return {};
  };

  @Input()
  public tableContainerStyle: { [key: string]: string } = {};

  @Input()
  public compactStyle: boolean = false;

  public rowTransformation: {
    initial: T[],
    hidden: T[],
    filter: T[],
    sort: T[],
    group: (T | Group)[],
    groupExpand: (T | Group)[],
    select: (T | Group)[],
    current: (T | Group)[]
  } = {
    initial: [],
    hidden: [],
    filter: [],
    group: [],
    sort: [],
    groupExpand: [],
    select: [],
    current: []
  };

  public columnTransformation: {
    initial: GemTableColumn[],
    current: GemTableColumn[],
    hidden: GemTableColumn[]
  } = {
    initial: [],
    current: [],
    hidden: []
  };

  @Input()
  public set rows(value: T[]) {
    // this.rowTransformation.initial = copyByValue(value);
    this.rowTransformation.initial = value;
    this.rowTransformation.current = this.rowTransformation.initial;
    this.transformRows();
  }

  public get rowData(): (T | Group)[] {
    return this.rowTransformation.current;
  }

  @Input()
  public set columns(value: GemTableColumn[]) {
    if (!value) {
      return;
    }
    // this.columnTransformation.initial = copyByValue(value);
    this.columnTransformation.initial = value;
    this.extendColumnDefinition();
    this.columnTransformation.hidden = this.columnTransformation.initial.filter(
      (val) => !val.hidden
    );
    this.columnTransformation.current = this.columnTransformation.hidden;
    this.reinitCreateDummy();
  }

  public get columns(): GemTableColumn[] {
    return this.columnTransformation.current;
  }

  public get filterColumnIds(): string[] {
    return this.columnIds.map((c) => c + "filter");
  }

  public get columnIds(): string[] {
    return [
      ...(this.hasIconsCol() ? ["iconsCol"] : []),
      ...this.columns.map((c) => c.id),
    ];
  }

  public get groupColumnIds(): string[] {
    return this.columnIds.map((c) => c + "group");
  }

  public get addNewColumnIds(): string[] {
    return this.columnIds.map((c) => c + "addNew");
  }

  public get firstEditableColumn(): GemTableColumn {
    return this.columns.find((c) => !c.uneditable);
  }

  ngAfterViewInit() {
    this.canDelete = this.delete.observed;
    // this.canAddNextRow = this.add.observed;
    this.canUpdate = this.update.observed;
    this.canCreate = this.create.observed;
    this.cdRef.detectChanges();
  }

  private extendColumnDefinition() {
    this.columnTransformation.initial.forEach((c: GemTableColumn) => {
      c.id = getTableColumnId(c);
      if (c.resizable !== false) {
        c.resizable = true;
      }
      // TODO: extend on multilevel grouping
      if (c.groupings) {
        c.groupings.forEach((_) => {
          this.groupColumns.push(c);
        });
      }
    });
  }

  private transformRows(step: string = "initial") {
    if (!this.rowTransformation.initial) {
      return;
    }
    switch (step) {
      case "initial": {
        this.applyHideRows();
        this.applyFilter();
        this.applyFilterColumn();
        this.applySort();
        this.applyGroup();
      }
      case "select": {
        this.applySelect();
      }
      case "toggleGroupExpand": {
        this.applyToggleGroupExpand();
      }
      /*
      case "addNextRow": {
        this.applyAddNextRow();
      }
      /**/
    }
    this.rowTransformation.current = this.rowTransformation.groupExpand;
  }

  @Input()
  public hasColumnFooter: boolean = false;

  @Input()
  public footerTemplate: TemplateRef<any>;

  public hasSettingsButton(): boolean {
    return this.canHideColumns || this.isGroupable || this.hasShowInactiveBtn;
  }

  public hasIconsCol(): boolean {
    return this.canUpdate
      || this.canDelete
      // || this.canAddNextRow
      || this.canCreate
      || this.selectable;
  }

  // -------------------- row hide (start) --------------------
  private _activeRow: (row: T) => boolean = (_) => true;

  @Input()
  public set activeRow(func: (row: T) => boolean) {
    this._activeRow = func;
    this.transformRows();
  }

  @Input()
  public inactiveRowStyle: { [key: string]: string } = {};

  public get activeRow(): (row: T) => boolean {
    return this._activeRow;
  }

  public presentInactiveRow(): boolean {
    if (!this.rowTransformation.initial) {
      return false;
    }
    return this.rowTransformation.initial.some((r: any) => !this.activeRow(r));
  }

  public areRowsHidden: boolean = false;
  public areRowsHiddenByDefault: boolean = true;

  public toggleHideRows() {
    this.areRowsHidden = !this.areRowsHidden;
    this.transformRows();
  }

  private applyHideRows() {
    const value = this.rowTransformation.initial;

    if (this.areRowsHidden) {
      this.rowTransformation.hidden = value.filter(this.activeRow);
    } else {
      this.rowTransformation.hidden = value;
    }
  }
  // -------------------- row hide (end) --------------------

  // -------------------- row filter (start) --------------------
  private filterValue: string;

  @Input() public isFilterable: boolean = false;

  public filter(event: KeyboardEvent) {
    const inputValue = (<HTMLInputElement>event.target).value;
    this.filterValue = inputValue.trim().toLowerCase();
    this.transformRows();
  }

  private applyFilter() {
    const value = this.rowTransformation.hidden;
    if (!!this.filterValue) {
      this.rowTransformation.filter = value.filter(this.filterRow.bind(this));
    } else {
      this.rowTransformation.filter = value;
    }
  }

  private filterRow(row): boolean {
    let satisfySearch = false;
    const valueSet = [];

    this.columns.forEach((col: GemTableColumn) => {
      let value = getTableRowProp(row, col.prop);
      // TODO: should this be changed?
      if (
        col.displayValue !== undefined &&
        !col.filterByModelValue &&
        !!col.displayValue(value, row, col.prop) &&
        typeof col.displayValue(value, row, col.prop) === "string"
      ) {
        valueSet.push(col.displayValue(value, row, col.prop).toLowerCase());
      } else {
        value = value === null || value === undefined ? "" : value;
        // TODO: array handling should be done as a part of displayValue case
        if (Array.isArray(value)) {
          valueSet.push(
            ...value
              .filter((v) => v !== null && v !== undefined)
              .map((v) => v.toString().toLowerCase())
          );
        } else {
          valueSet.push(value.toString().toLowerCase());
        }
      }
    });

    valueSet.forEach((value) => {
      if (typeof value === "string" && value.includes(this.filterValue)) {
        satisfySearch = true;
      }
    });
    return satisfySearch;
  }
  // -------------------- row filter (end) --------------------

  // -------------- second header filter row (start) -----------------

  // Variables to open the second header
  // @Input()
  public hasFilterHeader: boolean = false;
  public expandedSecondHeader: boolean = true;

  // Variables used in the methods below
  private columnFilters = new Array<FilterValue>();
  private firstBoxField: string = "";
  private secondBoxField: string = "";
  private secondRangeFF: string = "";
  private secondRangeSF: string = "";
  private currentBoxField: string = "";
  private currentSecondRangeField: string = "";
  private columnToFilter: any;
  public firstFilterOption: string = "default";
  public secondFilterOption: string = "default";
  private currentFilterOption: string;
  private operator: string;
  private columnAlreadyInArray: boolean = false;

  public clearFilters: boolean;

  public expandSecondHeader() {
    this.expandedSecondHeader = !this.expandedSecondHeader;
  }

  /*  To apply all filters depending on the global filter
   is made an Array which is changed by every change of a local filters.
   In setSearchArray method the changes are saved in the Array and than
   from filterColumn is called the method transformRows which applies all
   filter, sorting, etc. options on the table
  */

  public setSearchArray(
    inputValues: {
      option: any;
      value: any;
      radio: any;
      filter: string;
      secondRange?: boolean;
    },
    column: any
  ) {
    if (!!inputValues.radio) {
      this.operator = inputValues.radio;
    }
    this.columnFilters.forEach((value, index) => {
      if (value.column.name == column.name) {
        if (inputValues.filter == "first") {
          if (inputValues.secondRange) {
            value.secondRangeFirstFilter = inputValues.value
              .toString()
              .trim()
              .toLowerCase();
          } else {
            if (!!inputValues.option) {
              value.firstOption = inputValues.option;
            } else {
              value.firstOption = null;
            }
            if (!!inputValues.value) {
              const input = inputValues.value.toString();
              value.firstValue = input.trim().toLowerCase();
            } else {
              value.firstValue = null;
            }
            value.secondValue = null;
            value.secondOption = null;
          }
        } else if (inputValues.filter == "second") {
          if (inputValues.secondRange) {
            value.secondRangeSecondFilter = inputValues.value
              .toString()
              .trim()
              .toLowerCase();
          } else {
            if (!!inputValues.option) {
              value.secondOption = inputValues.option;
            }
            if (!!inputValues.value) {
              const input = inputValues.value.toString();
              value.secondValue = input.trim().toLowerCase();
            }
          }
        }
        this.columnAlreadyInArray = true;
      }
    });
    if (!this.columnAlreadyInArray) {
      // Add new column
      if (!!inputValues.value) {
        const input = inputValues.value.toString();
        this.columnFilters.push({
          column: column,
          firstValue: input.trim().toLowerCase(),
          secondValue: null,
          firstOption: inputValues.option,
          secondOption: null,
          secondRangeFirstFilter: null,
          secondRangeSecondFilter: null,
        });
      }
    }
    this.columnAlreadyInArray = false;
  }

  public filterColumn(
    inputValues: {
      option: any;
      value: any;
      radio: any;
      filter: string;
      secondRange?: boolean;
    },
    column: any
  ) {
    this.clearFilters = false;
    this.setSearchArray(inputValues, column);
    this.transformRows();
  }

  /* Is called from transformRows Method and it applies for every column the set filter options*/
  private applyFilterColumn() {
    this.columnFilters.forEach((value) => {
      this.firstBoxField = value.firstValue;
      this.secondBoxField = value.secondValue;
      this.firstFilterOption = value.firstOption;
      this.secondFilterOption = value.secondOption;
      this.secondRangeFF = value.secondRangeFirstFilter;
      this.secondRangeSF = value.secondRangeSecondFilter;

      this.columnToFilter = value.column;
      this.apply();
    });
  }

  /* Filters based on the defined variables for the Box-fields, Options and Operators*/
  private apply() {
    const value = this.rowTransformation.filter;
    if (!!this.firstBoxField) {
      if (!!this.secondRangeFF) {
        this.currentSecondRangeField = this.secondRangeFF;
      }
      this.currentFilterOption = this.firstFilterOption;
      this.currentBoxField = this.firstBoxField;
      const newRows = value.filter(this.filterColumnsRow.bind(this));

      this.rowTransformation.filter = newRows;
      if (!!this.secondBoxField) {
        if (!!this.secondRangeSF) {
          this.currentSecondRangeField = this.secondRangeSF;
        }
        this.currentFilterOption = this.secondFilterOption;
        this.currentBoxField = this.secondBoxField;

        if (this.operator == "ODER") {
          let value2 = value.filter(this.filterColumnsRow.bind(this));
          value2 = this.removeDuplicates(value2, this.rowTransformation.filter);
          this.rowTransformation.filter = value2.concat(newRows);
        } else if (this.operator == "UND") {
          const value2 = this.rowTransformation.filter;
          this.rowTransformation.filter = value2.filter(
            this.filterColumnsRow.bind(this)
          );
        }
      }
    }
  }

  private removeDuplicates(value2, value1): any {
    return value2.filter((val) => {
      return !value1.includes(val);
    });
  }

  private filterColumnsRow(row): boolean {
    let satisfySearch = false;
    const valueSet = [];
    let value = getTableRowProp(row, this.columnToFilter.prop);

    if (
      this.columnToFilter.displayValue !== undefined &&
      !this.columnToFilter.filterByModelValue &&
      !!this.columnToFilter.displayValue(
        value,
        row,
        this.columnToFilter.prop
      ) &&
      typeof this.columnToFilter.displayValue(
        value,
        row,
        this.columnToFilter.prop
      ) === "string"
    ) {
      if (this.columnToFilter.type == 2) {
        valueSet.push(dateAsString(stringAsDate(value), DateTimeFormat.MACOCO));
      } else if (this.columnToFilter.type == 1) {
        valueSet.push(
          this.columnToFilter
            .displayValue(value, row, this.columnToFilter.prop)
            .toString()
            .replace(":", "")
        );
      } else {
        valueSet.push(
          this.columnToFilter
            .displayValue(value, row, this.columnToFilter.prop)
            .toLowerCase()
        );
      }
    } else {
      value = value === null || value === undefined ? "" : value;
      // TODO: array handling should be done as a part of displayValue case
      if (Array.isArray(value)) {
        valueSet.push(
          ...value
            .filter((v) => v !== null && v !== undefined)
            .map((v) => v.toString().toLowerCase())
        );
      } else {
        valueSet.push(value.toString().toLowerCase());
      }
    }

    if (this.columnToFilter.type == 1 || this.columnToFilter.type == 2) {
      if (!!this.currentBoxField) {
        satisfySearch = this.filterBySign(valueSet);
      }
    } else {
      valueSet.forEach((value) => {
        if (typeof value === "string" && value.includes(this.firstBoxField)) {
          satisfySearch = true;
        }
      });
    }
    return satisfySearch;
  }

  /* Transforms the values into int values and compares them based on the filter-option */
  private filterBySign(valueSet: any): boolean {
    let satisfySearch = false;
    valueSet.forEach((value) => {
      if (typeof value === "string") {
        switch (this.currentFilterOption) {
          case "equal": {
            if (value.includes(this.currentBoxField)) {
              satisfySearch = true;
            }
            break;
          }
          case "notEq": {
            if (!value.includes(this.currentBoxField)) {
              satisfySearch = true;
            }
            break;
          }
          case "greater": {
            if (parseInt(this.currentBoxField) < parseInt(value)) {
              satisfySearch = true;
            }
            break;
          }
          case "greaterEq": {
            if (parseInt(this.currentBoxField) <= parseInt(value)) {
              satisfySearch = true;
            }
            break;
          }
          case "less": {
            if (parseInt(this.currentBoxField) > parseInt(value)) {
              satisfySearch = true;
            }
            break;
          }
          case "lessEq": {
            if (parseInt(this.currentBoxField) >= parseInt(value)) {
              satisfySearch = true;
            }
            break;
          }
          case "range": {
            if (
              parseInt(this.currentBoxField) <= parseInt(value) &&
              parseInt(this.currentSecondRangeField) >= parseInt(value)
            ) {
              satisfySearch = true;
            }
            break;
          }
        }
      }
    });
    return satisfySearch;
  }

  public resetFilters(): void {
    this.clearFilters = true;
    this.columnFilters = [];
    this.transformRows();
  }
  // -------------- second header filter row (end) -----------------

  // -------------------- row sort (start) --------------------
  private _sortDirection: { [key: string]: SortDirection }[] = [];

  @Input()
  public set sortDirection(value: { [key: string]: SortDirection }[]) {
    let i = value.length;
    // add sortLevels to all columns which are going to be sorted
    value.forEach((val) => {
      const columnId = Object.keys(val)[0];
      this.columns.forEach((col) => {
        if (col.id === columnId) {
          col.sortLevel = i;
          i--;
        }
      });
    });
    this._sortDirection = value;
    this.transformRows();
  }

  public get sortDirection(): { [key: string]: SortDirection }[] {
    return this._sortDirection;
  }

  public sort(column: GemTableColumn) {
    /* TODO: enable sort
    const columnSortIndex: number = this.sortDirection.findIndex(c => !!c[column.id]);
    if (columnSortIndex > -1) {
      const sort = this.sortDirection.splice(columnSortIndex, 1)[0];
      if (sort[column.id] === SortDirection.ASCENDING) {
        sort[column.id] = SortDirection.DESCENDING;
        this.sortDirection.push(sort);
      }
    } else {
      const sort = {};
      sort[column.id] = SortDirection.ASCENDING;
      this.sortDirection.push(sort);
    }
    this.transformRows();
    /**/
  }
  public static compareDefault(aprop: any, bprop: any): number {
    if (aprop instanceof Date && bprop instanceof Date) {
      return aprop.getTime() - bprop.getTime();
    } else if (typeof aprop === "number" && typeof bprop === "number") {
      return aprop - bprop;
    } else {
      return aprop.toString().localeCompare(bprop.toString());
    }
  }
  private applySort() {
    const value = this.rowTransformation.filter;
    if (this.sortDirection.length) {
      this.rowTransformation.sort = [...value];
      this.sortDirection.forEach((sort: { [key: string]: SortDirection }) => {
        const columnId: string = Object.keys(sort)[0];
        const sortColumn = this.columns.find((c) => c.id === columnId);
        if (sortColumn == null) {
          console.error(`could not find sort column with id ${columnId}`);
          return;
        }
        let comparator = sortColumn.sort ? sortColumn.sort.comparator : null;
        this.rowTransformation.sort = this.rowTransformation.sort.sort(
          (a: T, b: T) =>
            this.compareRows(a, b, sortColumn.prop, sort[columnId], comparator)
        );
      });
    } else {
      this.rowTransformation.sort = value;
    }
  }

  public compareRows(
    a: any,
    b: any,
    prop: any,
    direction?: SortDirection,
    comparator?: (a: any, b: any) => number
  ): number {
    const desc = direction === SortDirection.DESCENDING;
    const aprop = getTableRowProp(a, prop);
    const bprop = getTableRowProp(b, prop);

    if (aprop === undefined || aprop === null) {
      return -1 * (desc ? -1 : 1);
    }
    if (bprop === undefined || bprop === null) {
      return 1 * (desc ? -1 : 1);
    }
    let res: number = 0;
    if (comparator != null) {
      res = comparator(aprop, bprop);
    } else if (aprop instanceof Date && bprop instanceof Date) {
      res = aprop.getTime() - bprop.getTime();
    } else if (typeof aprop === "number" && typeof aprop === "number") {
      res = aprop - bprop;
    } else {
      res = aprop.toString().localeCompare(bprop.toString());
    }
    return res * (desc ? -1 : 1);
  }
  // -------------------- row sort (end) --------------------

  // -------------------- row group (start) --------------------
  public groupColumns: GemTableColumn[] = [];

  public get groupColumnNames(): string[] {
    return this.groupColumns.map((c) => c.name);
  }

  public get groupColumnProps(): string[] {
    return this.groupColumns.map((c) => c.prop);
  }

  public group(column: GemTableColumn) {
    this.groupColumns = [];
    if (column.groupings) {
      column.groupings.forEach((_) => this.groupColumns.push(column));
    } else {
      this.groupColumns.push(column);
    }
    // TODO: multi grouping
    /*
    const groupColumnIndex = this.groupColumns.findIndex(c => c.id === column.id);
    if (groupColumnIndex > -1) {
      const groupColumnProp = this.groupColumns.splice(groupColumnIndex, 1)[0];
      this.groupColumns.push(groupColumnProp);
    } else {
      this.groupColumns.push(column);
    }
    /**/
    this.transformRows();
  }

  public ungroup() {
    this.groupColumns = [];
    this.transformRows();
  }

  private applyGroup() {
    const value = this.rowTransformation.sort;
    if (this.groupColumns.length) {
      this.rowTransformation.group = this.addGroups(value, this.groupColumns);
    } else {
      this.rowTransformation.group = value;
    }
  }

  private addGroups(data: T[], groupColumns: GemTableColumn[]): Array<T | Group> {
    const rootGroup = new Group();
    return this.getSublevel(data, 0, groupColumns, rootGroup);
  }

  private getSublevel(
    data: T[],
    level: number,
    groupColumns: GemTableColumn[],
    parent: Group
  ): any[] {
    const groupColumnsProp = groupColumns.map((c) => c.prop);
    const currentColumnProp = groupColumnsProp[level];

    const firstPropLevel = groupColumnsProp.indexOf(currentColumnProp);
    const lastPropLevel = groupColumnsProp.lastIndexOf(currentColumnProp);

    // Recursive function, stop when there are no more levels.
    if (level >= groupColumnsProp.length) {
      return data;
    }

    const currentColumn = groupColumns[level];
    const displayValue = currentColumn.displayValue;

    let groups: Group[];
    groups = this.uniqueBy(
      data.map((row) => {
        const result = new Group();
        result.level = level + 1;
        result.parent = parent;
        for (let i = 0; i <= level; i++) {
          if (level < lastPropLevel) {
            const grouping = currentColumn.groupings[level - firstPropLevel];
            result.extension = grouping.criterion(row[groupColumnsProp[i]]);
            if (grouping.style) {
              result.style = grouping.style;
            }
          } else {
            if (displayValue) {
              result.props[groupColumnsProp[i]] = displayValue(
                row[groupColumnsProp[i]]
              );
            } else {
              result.props[groupColumnsProp[i]] = row[groupColumnsProp[i]];
            }
          }
        }
        return result;
      }),
      JSON.stringify
    );

    let subGroups = [];
    groups.forEach((group) => {
      let rowsInGroup: T[];
      rowsInGroup = data.filter((row) => {
        if (level < lastPropLevel) {
          const val = row[currentColumnProp];
          return Utils.containsKeyValues(
            group.extension,
            currentColumn.groupings[level - firstPropLevel].criterion(val)
          );
        }
        if (displayValue) {
          return (
            group.props[currentColumnProp] ===
            displayValue(row[currentColumnProp])
          );
        } else {
          return group.props[currentColumnProp] === row[currentColumnProp];
        }
      });
      this.addGroupSummary(group, rowsInGroup);
      const subGroup = this.getSublevel(
        rowsInGroup,
        level + 1,
        groupColumns,
        group
      );
      subGroup.unshift(group);
      subGroups = subGroups.concat(subGroup);
    });
    return subGroups;
  }

  public isGroupedByColumn(column: GemTableColumn) {
    return this.groupColumns.some((c) => c.id === column.id);
  }

  private addGroupSummary(group: Group, rows: T[]) {
    this.columns
      .filter((c) => !!c.summarize)
      .forEach((c) => {
        group.summary[c.prop] = this.summarizeGroup(c, rows);
      });
  }

  private summarizeGroup(column: GemTableColumn, rows: T[]): string {
    let summary: string;

    let arr: T[] = rows;
    if (!column.summarize.extended) {
      arr = rows.map((r: T) => getTableRowProp(r, column.prop));
    }

    summary = column.summarize.function(arr);
    if (column.displayValue) {
      summary = column.displayValue(summary);
    }
    return summary;
  }

  private uniqueBy(a, key) {
    const seen = {};
    return a.filter((item) => {
      const k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }

  // -------------------- row group expansion (start) --------------------
  private groupExpansions: Group[] = [];

  public toggleGroupExpandArray(groups: Group[]) {
    groups.forEach((group) => {
      this.setGroupExpansion(group);
    });

    this.transformRows();
  }

  public toggleGroupExpand(group: Group) {
    this.setGroupExpansion(group);
    this.transformRows("toggleGroupExpand");
  }

  private applyToggleGroupExpand() {
    const value = this.rowTransformation.select;
    this.updateGroupExpansions(value.filter((v): v is Group => v instanceof Group));
    this.rowTransformation.groupExpand = this.getVisibleRows(value);
  }

  // IMPORTANT: Assuming that group rows are defined before the rows they group
  private getVisibleRows(data: Array<T | Group>): Array<T | Group> {
    let expanded: boolean = true;
    return data.filter((row: T | Group) => {
      if (row instanceof Group) {
        expanded = row.expanded && row.visible;
        if (row.visible) {
          return true;
        }
      }

      return expanded;
    });
  }

  private setGroupExpansion(group: Group) {
    const groupIndex = this.groupExpansions.findIndex((e) =>
      this.isEqualGroup(group, e)
    );

    group.expanded = !group.expanded;
    if (groupIndex > -1) {
      this.groupExpansions.splice(groupIndex, 1, group);
    } else {
      this.groupExpansions.push(group);
    }
  }

  private updateGroupExpansions(groups: Group[]) {
    // this.validateGroupExpansions(groups);
    this.restoreGroupExpansions(groups);
  }

  /*
  private validateGroupExpansions(groups: Group[]) {
    for (let i = this.groupExpansions.length - 1; i >= 0; --i) {
      const group: Group = this.groupExpansions[i];
      const valid: boolean = groups.some((e) => this.isEqualGroup(group, e));
      if (!valid) {
        this.groupExpansions.splice(i, 1);
      }
    }
  }
  /** */

  private restoreGroupExpansions(groups: Group[]) {
    for (const group of groups) {
      const expansion = this.groupExpansions.find((e) =>
        this.isEqualGroup(group, e)
      );
      if (expansion) {
        group.expanded = expansion.expanded;
      }
    }
  }

  private isEqualGroup(group: Group, expansion: Group) {
    let equal: boolean = Utils.containsKeyValues(expansion.props, group.props);
    if (group.extension && expansion.extension) {
      equal =
        equal && Utils.containsKeyValues(expansion.extension, group.extension);
    }
    return equal;
  }

  /*
  private getCurrentGroups(): Group[] {
    return this.rowTransformation.current.filter((r) => r instanceof Group);
  }
  /** */
  // -------------------- row group expansion (end) --------------------

  public isGroup(index, item): boolean {
    return item instanceof Group;
  }
  // -------------------- row group (end) --------------------

  // -------------------- row selection (start) --------------------
  private _selection: (T | Group)[] = [];

  @Input()
  public set selection(value: (T | Group)[]) {
    this._selection = [];
    this._selection.push(...value);
    this.transformRows("select");
  }

  @Output()
  public batchModeToggle: EventEmitter<boolean> = new EventEmitter();
  @Output()
  public rowsSelect: EventEmitter<T[]> = new EventEmitter();

  private _selectionType: TableSelectionType = TableSelectionType.NONE;

  @Input()
  public set selectionType(value: TableSelectionType) {
    this.selectable = value !== TableSelectionType.NONE;
    this._selectionType = value;
  }

  public get selectionType(): TableSelectionType {
    return this._selectionType;
  }

  public selectable: boolean = false;

  public selectionStatus: TableSelectionStatus = TableSelectionStatus.NONE;

  /** Whether the number of selected elements matches the total number of rows. */
  public isAllSelected() {
    return this.selectionStatus === TableSelectionStatus.ALL;
  }

  public isSomeSelected() {
    return this.selectionStatus === TableSelectionStatus.SOME;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  public masterToggleSelect(event: MatCheckboxChange) {
    event.checked
      ? this.toggleSelectRows(this.rowTransformation.group, true)
      : this.toggleSelectRows(this.rowTransformation.group, false);
  }

  // -------------------- advanced editing batch (start) --------------------

  private _batchMode: boolean = false;

  @Input()
  public set batchMode(value: boolean) {
    this._batchMode = value;
  }

  public get batchMode(): boolean {
    return this._batchMode;
  }

  public toggleBatchMode(mode: MatSlideToggleChange) {
    this.batchMode = mode.checked;
    this.batchModeToggle.emit(mode.checked);
  }

  // -------------------- advanced editing selection (start) --------------------

  public applySelect() {
    const value = this.rowTransformation.group;
    this.rowTransformation.select = value;
    if (this.selectable && this.batchMode) {
      this.rowTransformation.select.forEach((row: T) => {
        if (this._selection.includes(row)) {
          row.gemSelected = true;
        } else {
          row.gemSelected = false;
        }
      });
      this.updateSelectionStatus();
    }
  }

  public toggleSelect(event: MatCheckboxChange, row: T | Group) {
    this.toggleSelectRows([row], event.checked);
  }

  private toggleSelectRows(rows: (T | Group)[], selected: boolean) {
    let allSelected = [];
    if (selected) {
      allSelected = this._selection.concat(rows);
    } else {
      allSelected = this._selection.filter((r) => !rows.includes(r));
    }
    this.selection = allSelected;
    this.updateSelectionStatus();
    this.rowsSelect.emit(allSelected);
  }

  private updateSelectionStatus() {
    if (this.rowTransformation.select.every((r) => r.gemSelected)) {
      this.selectionStatus = TableSelectionStatus.ALL;
    } else if (this.rowTransformation.select.some((r) => r.gemSelected)) {
      this.selectionStatus = TableSelectionStatus.SOME;
    } else {
      this.selectionStatus = TableSelectionStatus.NONE;
    }
  }

  // -------------------- advanced editing selection (end) --------------------

  // -------------------- advanced editing batch (end) --------------------

  // -------------------- row selection (end) --------------------

  // -------------------- column hide (start) --------------------
  private hiddenColumns: string[] = [];

  public toggleHideColumn(id) {
    const columnIndex = this.hiddenColumns.indexOf(id);
    if (columnIndex > -1) {
      this.hiddenColumns.splice(columnIndex, 1);
    } else {
      this.hiddenColumns.push(id);
    }
  }

  public isHiddenColumn(column: GemTableColumn) {
    return column.hidden || this.hiddenColumns.some((id) => column.id === id);
  }
  // -------------------- column hide (end) --------------------

  // -------------------- column reorder (start) --------------------
  private draggedColumnPosition;
  public draggedColumnId: any;

  // TODO: remove. This tests the performance increase when the rows are removed
  // and added back to the table when reordering columns.
  /*
  public reorder() {
    this.rowTransformation.current = [];
    this.columns.splice(2, 0, this.columns.splice(3, 1)[0]);
    this.transformRows();
  }
  /**/

  public dragStart(event, column) {
    const columnIndex = this.columns.findIndex((c) =>
      this.isEqualColumn(c, column)
    );
    this.draggedColumnPosition = columnIndex;
    if (columnIndex > -1) {
      event.dataTransfer.dropEffect = "move";
    } else {
      console.error(
        `Unable to drag column, column ${column.name} was not found`
      );
    }
  }

  public drop(event, column) {
    const columnIndex = this.columns.findIndex((c) =>
      this.isEqualColumn(c, column)
    );
    if (this.draggedColumnPosition > -1 && columnIndex > -1) {
      this.reorderColumns(this.draggedColumnPosition, columnIndex);
      this.draggedColumnId = null;
    } else {
      console.error(`Unable to drop column`);
    }
  }

  public dragOver(event, column: GemTableColumn) {
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
    this.draggedColumnId = column.id;
  }

  public reorderColumns(currentPosition: number, dropPosition: number) {
    if (currentPosition !== dropPosition) {
      this.columns.splice(
        dropPosition,
        0,
        this.columns.splice(currentPosition, 1)[0]
      );
    }
    console.log(this.columns);
  }
  // -------------------- column reorder (end) --------------------

  public columnIdentifier(index: number, item: GemTableColumn) {
    return item.id;
  }

  public rowIdentifier(index: number, item: Group | T) {
    return index;
  }

  // protected commandManager: CommandManager;

  constructor(
    // private ngZone: NgZone,
    // private router: Router,
    // private contextMenuService: ContextMenuService<T>,
    // protected _commandRestService: CommandRestService
    protected cdRef: ChangeDetectorRef
  ) {
    //this.commandManager = new CommandManager(this._commandRestService);
  }

  // -------------------- hotkey handling (start) --------------------
  private addNewFocused: boolean = false;

  public focusAddNew() {
    this.addNewFocused = true;
  }

  public unfocusAddNew() {
    this.addNewFocused = false;
  }

  @HostListener("keydown.enter", ["$event"])
  public handleKeyboardEvent(event: KeyboardEvent) {
    if (this.alwaysEdit && this.addNewFocused) {
      this.blur(event);
      // this.onCreate();
    } else if (!this.alwaysEdit && this.inputHasFocus()) {
      this.blur(event);
      // this.save();
    }
  }

  private blur(event: KeyboardEvent) {
    event.stopPropagation();
    (<HTMLElement>document.activeElement).blur();
  }

  public save(row: T) {
    if (this.editRowId === EMPTY_ROW_ID) {
      // this.saveAddNextRow();
    } else if (this.editRowId != null) {
      this.updateRow(row);
    }
  }

  @HostListener("keydown.escape", ["$event"])
  public handleKeyboardEventEscape(event: KeyboardEvent) {
    if (!this.inputHasFocus()) {
      return;
    }

    // this.cancel();
  }

  public cancel(row: T) {
    if (this.editRowId === EMPTY_ROW_ID) {
      // this.cancelAddNextRow();
    } else if (this.editRowId != null) {
      this.cancelUpdateRow(row);
    }
  }

  // TODO: handle first empty row, calculation is more complicated
  private inputHasFocus(): boolean {
    return this.editRowId != null;
  }
  // -------------------- hotkey handling (end) --------------------

  // -------------------- row edit (start) --------------------
  canUpdate: boolean = false;

  public editRowId: number | string;

  @Output()
  public edit: EventEmitter<T> = new EventEmitter();

  public enableEditMode(row: T, column: GemTableColumn) {
    if (
      this.isInEditMode(row) ||
      this.editRowId != null ||
      this.alwaysEdit ||
      !this.canUpdate ||
      this.staticRow(row) ||
      this.batchMode
    ) {
      return;
    }

    this.focus(column);
    /*
    if (this.form.valid) {
      // save previously focused row, when user clicks away
      this.saveUnfocusedRow();
      this.focus(column);
      // if clicked sell had an invalid value, explicitly set an invalid value to a control
      if (this.invalidRowMap.has(row.gemId)) {
        let invalidRow = this.invalidRowMap.get(row.gemId);
        for (const prop in invalidRow) {
          let col = invalidRow[prop];
          this.columnControlMap[prop].setValue(col.actualValue);
        }
        event.stopPropagation();
      }
      // show errors on edit
      this.form.markAsCDirty();
      this.form.markAsCTouched();
      this.form.validate();

      this.editRowId = row.gemId;
      this.edit.emit(row);
    }
    */
    this.editRowId = row.gemId;
    //Reflect.defineMetadata(EDIT_CANCEL, this.cancelEditRow.bind(this), row);
    //Reflect.defineMetadata(EDIT_SAVE, this.saveEditRow.bind(this), row);
    this.edit.emit(row);
  }

  public disableEditMode() {
    this.editRowId = null;
  }

  public isInEditMode(row: T): boolean {
    return !!row && this.editRowId != null && this.editRowId === row.gemId;
  }
  // -------------------- row focus (start) --------------------

  public focusHandler: { id: string; active: boolean };

  public focus(column: GemTableColumn) {
    this.focusHandler = {
      id: column.id,
      active: true,
    };
  }
  // -------------------- row focus (end) --------------------
  // -------------------- context menu (start) --------------------

  @Input()
  public contextMenuItems: (item) => ContextMenuItem[];
  @Input()
  public rightClickMenu: TemplateRef<any>;
  @ViewChild("contextMenu") public contextMenu: GemContextMenuComponent<T>;

  public onContextMenu(
    $event: MouseEvent,
    item: { prop?: string | number; model: T; columns?: GemTableColumn[] },
    isInEditMode: boolean
  ): void {
    // let contextMenu: ContextMenuComponent<T>;
    let items = [];
    let isRow = false;

    if (this.contextMenuItems && !isInEditMode) {
      this.contextMenu.contextMenuActions = this.contextMenuItems(item);
      // contextMenu = <ContextMenuComponent<T>>this.contextMenu.template;
    } else if (this.rightClickMenu && !isInEditMode) {
      // Old right click menu
      // contextMenu = <any>this.rightClickMenu;
    }

    if (!(item.model instanceof Group)) {
      items.push(item.model);
    } else {
      this.rowTransformation.current.forEach((value) => {
        if (value === item.model) {
          isRow = true;
        } else if (value instanceof Group) {
          isRow = false;
        }

        if (isRow && !(value instanceof Group)) {
          items.push(value);
        }
      });
    }
    /*
    if (contextMenu && items.length > 0) {
      setTimeout(() => {
        this.contextMenuService.show.next({
          contextMenu: contextMenu,
          event: $event,
          item: items,
        });
      }, 0);
    }
    /**/
    $event.preventDefault();
  }

  public closeContextMenu() {
    // workaround to close context menu when a cell is clicked
    // prevents that the context menu remains open
    // https://github.com/isaacplmann/ngx-contextmenu/issues/122
    document.dispatchEvent(new Event("click"));
  }

  // -------------------- context menu (end) --------------------

  // -------------------- row edit save (start) --------------------
  @Output()
  public update: EventEmitter<T> = new EventEmitter();

  private updateRow(row: T) {
    this.update.emit(row);
    this.disableEditMode();
  }
  // -------------------- row edit save (end) --------------------

  // -------------------- row edit cancel (start) --------------------
  @Output()
  public cancelUpdate: EventEmitter<T> = new EventEmitter();

  private cancelUpdateRow(row: T) {
    this.cancelUpdate.emit(row);
    this.disableEditMode();
  }
  // -------------------- row edit cancel (end) --------------------
  // -------------------- row edit (end) --------------------

  // -------------------- row add (start) --------------------
  /*
  public canAddNextRow: boolean = false;

  @Output()
  public add: EventEmitter<{ row: T; nextRow: Subject<T> }> =
    new EventEmitter();

  private addNext: { id: number | string; row: T };

  public addNextRow(row: T) {
    // this.saveUnfocusedRow();
    this.disableEditMode();
    const nextRowExpectation = new Subject<T>();
    nextRowExpectation.subscribe((nextRow: T) => {
      this.createNextRow(row.gemId, this.createEmptyRow(nextRow));
      this.transformRows("addNextRow");
      this.enableEditMode(nextRow, this.getFirstEditableColumn());
      nextRowExpectation.unsubscribe();
    });
    this.add.emit({ row: row, nextRow: nextRowExpectation });
  }

  private createEmptyRow(row: T): T {
    row.gemId = EMPTY_ROW_ID;
    return row;
  }

  private createNextRow(rowId: number | string, nextRow: T): void {
    this.addNext = { id: rowId, row: nextRow };
  }

  private applyAddNextRow() {
    let value = this.rowTransformation.groupExpand;
    if (this.addNext) {
      const index =
        value.findIndex(
          (row: T | Group) =>
            !(row instanceof Group) && row.gemId === this.addNext.id
        ) + 1;
      value.splice(index, 0, this.addNext.row);
      value = value.slice();
    }
    this.rowTransformation.addNext = value;
  }

  // -------------------- row add save (start) --------------------
  @Output()
  public saveAdd: EventEmitter<T> = new EventEmitter();

  private saveAddNextRow() {
    // this.saveAdd.emit(this.form.getModelValue());
    this.resetAddNext();
    this.transformRows("addNextRow");
  }
  // -------------------- row add save (end) --------------------

  // -------------------- row add cancel (start) --------------------
  @Output()
  public cancelAdd: EventEmitter<void> = new EventEmitter();

  private cancelAddNextRow() {
    this.cancelAdd.emit();
    this.resetAddNext();
    this.transformRows();
  }

  private resetAddNext() {
    this.addNext = null;
    this.editRowId = null;
  }
  // -------------------- row add cancel (end) --------------------
  /**/
  // -------------------- row add (end) --------------------

  // -------------------- row delete (start) --------------------
  @Input()
  canDelete: boolean = false;

  @Output()
  public delete: EventEmitter<T> = new EventEmitter();

  public deleteRow(row: T) {
    // this.saveUnfocusedRow();
    this.disableEditMode();

    this.delete.emit(row);
  }
  // -------------------- row delete (end) --------------------

  // -------------------- advanced editing (start) --------------------

  /**
   * In inlineEditAll mode all rows in an invalid state are saved in this property
   */
  public invalidRowMap: Map<number, { [key: string]: InvalidCellDescription }> = new Map();

  private unfocusedRow: T;

  /**
   * Remeber row (cell), which loses focus, when user clicks away
   */
  public onFocusOut(row: T): void {
    this.unfocusedRow = row;
  }

  /**
   * Save the row, which user clicked away from
   */
  public saveUnfocusedRow(): void {
    if (this.unfocusedRow) {
      this.removeInvalidRow(this.unfocusedRow.gemId);
      // this.saveEdit.emit(res);

      this.unfocusedRow = null;
    }
  }

  /**
   * Save invalid row with its value in the invalidRowMap
   */
  /*
  storeInvalidRow(rowId: number) {
    let controlValues: { [key: string]: InvalidCellDescription } = {};
    for (const prop in this.columnControlMap) {
      const control = this.columnControlMap[prop];
      controlValues[prop] = {
        displayValue: control.displayValue,
        actualValue: control.getModelValue(),
      };
      if (control.invalid) {
        controlValues[prop].invalid = true;
        controlValues[prop].actualValue = control.actualValue;
        if (control.errors) {
          controlValues[prop].error = control.errors.error;
        }
      }
    }

    this.invalidRowMap.set(rowId, controlValues);
  }
  /**/

  /**
   * Delete invalidRowMap record
   */
  removeInvalidRow(rowId: number) {
    this.invalidRowMap.delete(rowId);
  }

  /**
   * Check if the cell contains value, which causes an invalid row state
   */
  isInvalidCell(rowId: number, columnProp: string): boolean {
    if (!this.invalidRowMap.has(rowId)) {
      return false;
    } else {
      const control = this.invalidRowMap.get(rowId)[columnProp];
      return !!control && control.invalid;
    }
  }
  // -------------------- advanced editing hotkeys (start) --------------------

  jumpCellUp(event, row: T, column: GemTableColumn) {
    this.jumpCell(event, row, this.findPrevRow(row.gemId), column);
  }

  jumpCellDown(event, row: T, column: GemTableColumn) {
    this.jumpCell(event, row, this.findNextRow(row.gemId), column);
  }

  jumpCellBack(event, row: T, column: GemTableColumn) {
    if (!column || !this.isFirstEditableColumn(column.id)) return;
    this.jumpCell(
      event,
      row,
      this.findPrevRow(row.gemId),
      this.getLastEditableColumn()
    );
  }

  jumpCellForward(event, row: T, column: GemTableColumn) {
    if (!column || !this.isLastEditableColumn(column.id)) return;
    this.jumpCell(
      event,
      row,
      this.findNextRow(row.gemId),
      this.getFirstEditableColumn()
    );
  }

  /**
   * Change cell focus to other cell
   */
  private jumpCell(
    event: Event,
    currentRow: T,
    jumpToRow: T,
    column: GemTableColumn
  ) {
    if (!column) return;
    event.preventDefault();

    const element = <HTMLElement>document.activeElement;
    element.blur();

    this.unfocusedRow = currentRow;
    (<HTMLElement>document.activeElement).blur();
    this.enableEditMode(jumpToRow, column);
  }

  private isLastEditableColumn(columnId: string) {
    const editableColumns = this.columns.filter((c) => !c.uneditable);
    const last = editableColumns[editableColumns.length - 1];
    return last.id && last.id === columnId;
  }

  private getLastEditableColumn(): GemTableColumn {
    const editableColumns = this.columns.filter((c) => !c.uneditable);
    return editableColumns[editableColumns.length - 1];
  }

  private isFirstEditableColumn(columnId: string) {
    const first = this.columns.filter((c) => !c.uneditable)[0];
    return first.id && first.id === columnId;
  }

  private getFirstEditableColumn(): GemTableColumn {
    return this.columns.filter((c) => !c.uneditable)[0];
  }

  private findNextRow(rowId: number): T | null {
    const rows: T[] = this.rowData.filter(
      (r: T | Group): r is T => !(r instanceof Group)
    );
    const index = rows.findIndex((r: T) => r.gemId === rowId);
    if (index > -1 && index < rows.length - 1) {
      return rows[index + 1];
    }
    return null;
  }

  private findPrevRow(rowId: number): T | null {
    const rows: T[] = this.rowData.filter(
      (r: T | Group): r is T => !(r instanceof Group)
    );
    const index = rows.findIndex((r: T) => r.gemId === rowId);
    if (index > 0) {
      return rows[index - 1];
    }
    return null;
  }

  // -------------------- advanced editing hotkeys (end) --------------------

  // -------------------- advanced editing (end) --------------------

  @Input() staticRow: (row: T) => boolean = (row: T) => false;

  @Input() extendableRow: (row: T) => boolean = (row: T) => true;

  @Input()
  public groupClass: (row: Group) => { [key: string]: string };

  public getGroupStyle(group: Group) {
    let style = {};
    if (this.groupClass) {
      style = this.groupClass(group);
    } else {
      style = group.style;
    }
    /*
    if (!this.activeRow(row)) {
      mergeIn(style, this.inactiveRowStyle);
    }
     */

    return style;
  }

  @Input()
  public groupTooltip: (row: T) => string;

  public getGroupTooltip(group: T) {
    if (this.groupTooltip) {
      return this.groupTooltip(group);
    }

    return undefined;
  }

  @Input()
  public rowClass: (row: T) => { [key: string]: string };

  public getColumnName(column) {
    return column.name.replace(/\&shy;/gi, "");
  }

  private isEqualColumn(c1: GemTableColumn, c2: GemTableColumn) {
    return c1.prop === c2.prop && c1.name === c2.name;
  }

  public getCellStyle(row: T) {
    let style = {};
    if (this.rowClass) {
      style = this.rowClass(row);
    }
    if (!this.activeRow(row)) {
      Utils.mergeIn(style, this.inactiveRowStyle);
    }
    return style;
  }

  public getCellValue(column: GemTableColumn, row: Partial<T>) {
    /*
    if (this.invalidRowMap.has(row.gemId)) {
      let invalidRow = this.invalidRowMap.get(row.gemId);
      if (invalidRow[column.prop]) {
        if (this.isInvalidCell(row.gemId, column.prop) || !column.displayValue) {
          return invalidRow[column.prop].displayValue;
        } else {
          return column.displayValue(invalidRow[column.prop].displayValue);
        }
      }
    }
    /**/
    // TODO: precalculate instead of getRowProp
    return column.displayValue !== undefined
      ? column.displayValue(this.getProp(row, column.prop), row, column.prop)
      : this.getProp(row, column.prop);
  }

  public getProp(obj, prop) {
    let ref = obj;
    if (typeof prop === "string") {
      let mapping = prop.split(".");
      while (mapping.length > 0 && ref !== undefined && ref !== null) {
        ref = ref[mapping.shift()];
      }
    }
    return ref;
  }

  // -------------------- advanced filter (start) --------------------

  @Input() public filterTemplate: TemplateRef<any>;
  public hasCustomSearch: boolean = false;

  public toggleCustomSearch() {
    this.hasCustomSearch = !this.hasCustomSearch;
  }

  // -------------------- advanced filter (end) --------------------

  // -------------------- config saving (start) --------------------

  // TODO: Needs to be globally defined. Could be used by other components
  /*
  private getPageUrl(): string {
    return this.router.url.replace(new RegExp("[0-9]", "g"), "");
  }
  /** */

  // -------------------- config saving (end) --------------------

  // TODO AGe: remove
  // -------------------- print config (start) --------------------

  @Input()
  public canHideColumns: boolean;
  @Input()
  public isGroupable: boolean;
  @Input()
  public hasShowInactiveBtn: boolean;

  // -------------------- print config (end) --------------------

  // -------------------- multi forms (start) --------------------

  private _alwaysEdit: boolean = false;

  @Input()
  public set alwaysEdit(value: boolean) {
    this._alwaysEdit = value;
  }

  public get alwaysEdit(): boolean {
    return this._alwaysEdit;
  }

  // -------------------- create (start) --------------------

  private _canCreate: boolean = false;

  createDummy: Partial<T>;

  reinitCreateDummy() {
    let res: Partial<T> = {};
    res.gemId = null;
    for (const col of this.columns) {
      let ref: Partial<T> = res;
      if (typeof col.prop === "string") {
        let mapping = col.prop.split(".");
        while (mapping.length > 0) {
          const prop = mapping.shift();
          if (mapping.length > 1) {
            ref[prop] = {};
            ref = ref[prop];
          } else {
            ref[prop] = null;
          }
        }
      }
    }
    this.createDummy = res;
  }

  @Input()
  public set canCreate(value: boolean) {
    this._canCreate = value;
    if (value) {
      this.reinitCreateDummy();
    }
  }

  public get canCreate(): boolean {
    return this._canCreate;
  }

  @Output()
  public create: EventEmitter<Partial<T>> = new EventEmitter();

  public createRow() {
    this.create.emit(this.createDummy);
  }

  @Output()
  public cancelCreate: EventEmitter<Partial<T>> = new EventEmitter();

  public cancelCreateRow() {
    this.cancelCreate.emit(this.createDummy);
    this.reinitCreateDummy();
  }
  // -------------------- create (end) --------------------

  // -------------------- multi forms (end) --------------------

  // -------------------- drag'n'drop (start) --------------------

  public onDragStart(event, row: T) {
    event.dataTransfer.setData("row", row);
    event.dataTransfer.setData("text/plain", row.gemId);
  }

  // -------------------- drag'n'drop (end) --------------------
}

// TODO: remove
@Directive()
export class GemTable{}
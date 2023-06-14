/* (c) https://github.com/MontiCore/monticore */

import { GemTableColumn } from "./gem-table.type";

// TODO: TableColumn could be made a class
export function getTableColumnId(column: GemTableColumn) {
  return column.prop + column.name;
}

// TODO: refine
export function getTableRowProp(row, prop) {
  let ref = row;
  if (typeof prop === "string") {
    let mapping = prop.split(".");
    while (mapping.length > 0 && ref !== undefined && ref !== null) {
      ref = ref[mapping.shift()];
    }
  }
  return ref;
}

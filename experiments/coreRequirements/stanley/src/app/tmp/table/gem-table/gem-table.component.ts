import { Component, Input } from "@angular/core";
import { TableColumn } from "../table-types";

@Component({
  selector: "gem-table",
  templateUrl: "./gem-table.component.html",
  styleUrls: ["./gem-table.component.scss"]
})
export class GemTable {

  @Input()
  rows: any[] = [];

  @Input()
  columns: TableColumn[] = []

}
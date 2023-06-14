import { Component } from "@angular/core";
import { GemTreeview as GemTreeviewTOP } from "./gem-treeview/gem-treeview.component-gen";

@Component({
  selector: "gem-treeview",
  templateUrl: "./gem-treeview.component.html",
  styleUrls: ["./gem-treeview.component.scss"],
})
export class GemTreeview extends GemTreeviewTOP {
  onexpand(item: any) {
    if (item.expanded) {
      item.expanded = !item.expanded;
      return;
    } else {
      if (item.children) {
        if (item.children.length > 0) {
          item.expanded = true;
        } else {
          item.expanded = false;
        }
      }
    }
  }
}

/* (c) https://github.com/MontiCore/monticore */

import { Component, OnInit, ViewChild } from "@angular/core";
import { ContextMenuComponent } from "@perfectmemory/ngx-contextmenu";

export interface ContextMenuItem {
  html?: (item) => string;
  click?: (item) => void;
  enabled?: (item) => boolean;
  visible: (item) => boolean;
  divider?: boolean;
  children?: ContextMenuItem[];
}

@Component({
  selector: "macoco-context-menu",
  templateUrl: "./gem-context-menu.component.html",
  styleUrls: ["./gem-context-menu.component.scss"],
})
export class GemContextMenuComponent<T> implements OnInit {
  @ViewChild("contextMenu") public template: ContextMenuComponent<T>;

  public contextMenuActions: ContextMenuItem[] = [];

  constructor() {}

  public ngOnInit(): void {}
}

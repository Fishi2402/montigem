/* (c) https://github.com/MontiCore/monticore */

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { config, GemTreeviewComponent } from 'mc/fenix/basic/GemTreeviewComponent';
import { GemTreeviewEntry } from 'mc/fenix/basic/gemtreeviewtypes/GemTreeviewEntry';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTreeview extends GemTreeviewComponent {
  _items: GemTreeviewEntry[] = [];
  _allExpanded: boolean = false;

  @Input()
  public set items(items: GemTreeviewEntry[]) {
    if (items == null) {
      return;
    }
    this._items = items;
    this.updateExpanded(this.items);
  }

  public get items(): GemTreeviewEntry[] {
    return this._items;
  }

  @Input()
  public set allExpanded(value: boolean) {
    if (value == null) {
      return;
    }
    this._allExpanded = value;
    this.updateExpanded(this.items);
  }

  public get allExpanded(): boolean {
    return this._allExpanded;
  }

  updateExpanded(items: GemTreeviewEntry[]) {
    if (this.allExpanded == null) {
      return;
    }
    for (const item of items) {
      if (item.getChildrenList()) {
        item.setExpanded(this.allExpanded);
        this.updateExpanded(item.getChildrenList());
      }
    }
  }

  @Output()
  public expand: EventEmitter<GemTreeviewEntry> = new EventEmitter();

  onExpand(item: GemTreeviewEntry) {
    if (item.isExpanded()) {
      item.setExpanded(!item.isExpanded());
    } else {
      if (item.getChildrenList()) {
        if (item.getChildrenList().length > 0) {
          item.setExpanded(true);
        } else {
          item.setExpanded(false);
        }
      }
    }
    this.expand.emit(item);
  }

}
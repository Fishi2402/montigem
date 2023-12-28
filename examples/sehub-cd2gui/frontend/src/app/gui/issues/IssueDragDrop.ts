/* (c) https://github.com/MontiCore/monticore */

import {CdkDragDrop, moveItemInArray, transferArrayItem} from '@angular/cdk/drag-drop';
import {Component, EventEmitter, Output} from '@angular/core';
import {IssueDragDropComponent,config} from '@src/gui/issues/IssueDragDropComponent';
import {Issue} from '@src/sehub/Issue';
import { DragDropModule } from "@angular/cdk/drag-drop";
import { IssueEntry } from "./IssueEntry";
@Component({
  standalone:true,
  imports:[...config.imports,DragDropModule,IssueEntry],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class IssueDragDrop extends IssueDragDropComponent {

  // TODO: fix generator error
  @Output()
  public override drop: EventEmitter<any> = new EventEmitter<any>();

  public ngOnInit(): void {
  }

  onDrop(event: CdkDragDrop<Issue[]>) {
    this.drop.emit(event?.previousContainer?.data?.[event.previousIndex]);
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
          event.previousContainer.data,
          event.container.data,
          event.previousIndex,
          event.currentIndex,
      );
    }
  }

}

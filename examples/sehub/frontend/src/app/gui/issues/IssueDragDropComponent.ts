/* (c) https://github.com/MontiCore/monticore */

import {
  CdkDragDrop,
  moveItemInArray, transferArrayItem
} from '@angular/cdk/drag-drop';
import { Component, Output, EventEmitter } from '@angular/core';
import { IssueDragDropComponentTOP } from '@src/gui/issues/IssueDragDropComponentTOP';
import { Issue } from '@src/sehub/Issue';

@Component({
  selector: 'issue-drag-drop',
  templateUrl: './IssueDragDrop.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./IssueDragDrop.scss',]
})
export class IssueDragDropComponent extends IssueDragDropComponentTOP {

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

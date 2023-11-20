/* (c) https://github.com/MontiCore/monticore */

import {CdkDragDrop, moveItemInArray, transferArrayItem} from '@angular/cdk/drag-drop';
import {Component, EventEmitter, Output} from '@angular/core';
import {IssueDragDropComponent} from '@src/gui/issues/IssueDragDropComponent';
import {Issue} from '@src/sehub/Issue';

@Component({
  selector: 'issue-drag-drop',
  templateUrl: './IssueDragDrop.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./IssueDragDrop.scss',]
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

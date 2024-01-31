/* (c) https://github.com/MontiCore/monticore */

import {AfterViewInit, Directive, ElementRef, Input, ChangeDetectorRef} from '@angular/core';

@Directive({
  selector: '[focusColumn]'
})
export class FocusColumnDirective implements AfterViewInit {

  constructor(
    private el: ElementRef,
    private cdRef: ChangeDetectorRef
  ) {}

  @Input() focusColumn: {handler: {id: string, active: boolean}, id: string};

  ngAfterViewInit() {
    if (this.focusColumn
      && this.focusColumn.handler
      && this.focusColumn.handler.id === this.focusColumn.id
      && this.focusColumn.handler.active) {
      this.focusColumn.handler.active = false;
      let input: HTMLInputElement = this.el.nativeElement.querySelector('input');
      if (input) {
        input.focus();
        this.cdRef.detectChanges();
      }
    }
  }
}
/* (c) https://github.com/MontiCore/monticore */

import {AfterViewInit, Directive, ElementRef, Input, ChangeDetectorRef} from '@angular/core';

@Directive({
  selector: '[enforceInputValue]'
})
export class EnforceInputValueDirective implements AfterViewInit {

  constructor(
    private el: ElementRef,
    private cdRef: ChangeDetectorRef
  ) {}

  @Input() enforceInputValue: boolean = false;

  @Input() forcedValue: any;
  @Input() forcedControl: any;

  ngAfterViewInit() {
    if (this.enforceInputValue) {
      let input: HTMLInputElement = this.el.nativeElement.querySelector('input');
      if (input) {
        setTimeout(() => {
          input.value = this.forcedValue;
          if (this.forcedControl) {
          console.log(this.forcedValue)
            this.forcedControl.setValue(this.forcedValue);
          }
          this.cdRef.detectChanges();
        });
      }
    }
  }
}
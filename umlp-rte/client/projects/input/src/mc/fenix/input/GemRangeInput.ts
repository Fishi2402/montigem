/* (c) https://github.com/MontiCore/monticore */

import { Component, ElementRef, EventEmitter, HostBinding, Output, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { config, GemRangeInputComponent } from '../../../../build/gui/mc/fenix/input/GemRangeInputComponent';

@Component({
  standalone: true,
  imports: [...config.imports, FormsModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemRangeInput extends GemRangeInputComponent {
  @HostBinding("attr.id")
  componentId: string;

  @ViewChild('rangeVElement', { static: false })
  rangeVElement: ElementRef;

  override _id: string = Math.random() + '';

  constructor(private elementRef: ElementRef) {
    super();
  }

  ngAfterViewInit(): void {
    if (this.vertical) {
      this.elementRef.nativeElement.style.transform = "rotate(90deg)";
    }
  }

  ngOnInit(): void {
    this.componentId = this.id;
    if (this.value === undefined) {
      this.value = this.minValue;
    }
  }

  @Output()
  valueChange = new EventEmitter<number>();

  onValueChange(event: number) {
    if(event === undefined){
      return;
    }

    this.value = event;
    this.valueChange.emit(this.value);
  }

  setValue(v:number) {
    super.setValue(v);
    if (!this.rangeVElement) return;

    const newValue =
      ((this.value - this.minValue) / (this.maxValue - this.minValue)) * 100
    ;
    const newPosition = 8 - newValue * 0.15;
    this.rangeVElement.nativeElement.innerHTML = `<span>${this.value}</span>`;
    this.rangeVElement.nativeElement.style.left = `calc(${newValue}% + (${newPosition}px))`;
  }
}

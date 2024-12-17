/* (c) https://github.com/MontiCore/monticore */

import { Component, ElementRef, EventEmitter, HostBinding, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { config, GemRangeInputComponent } from './build/gui/mc/fenix/input/GemRangeInputComponent';

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

  constructor(private elementRef: ElementRef) {
    super();
  }

  ngAfterViewInit(): void {
    this.setValue();
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
    this.value = event;
    this.valueChange.emit(this.value);
    this.setValue();
  }

  setValue() {
    const rangeV = this.elementRef.nativeElement.querySelector(
      `#${this.id}rangeV`
    );
    const newValue = Number(
      ((this.value - this.minValue) / (this.maxValue - this.minValue)) * 100
    );
    const newPosition = 8 - newValue * 0.15;
    rangeV.innerHTML = `<span>${this.value}</span>`;
    rangeV.style.left = `calc(${newValue}% + (${newPosition}px))`;
  }
}
/* (c) https://github.com/MontiCore/monticore */

import { Component, ElementRef, EventEmitter, HostBinding, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { config, GemDualRangeInputComponent } from './build/gui/mc/fenix/input/GemDualRangeInputComponent';

@Component({
  standalone: true,
  imports: [...config.imports, FormsModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemDualRangeInput extends GemDualRangeInputComponent {

  @HostBinding("attr.id")
  componentId: string;

  @Output()
  startChange = new EventEmitter<number>();

  @Output()
  endChange = new EventEmitter<number>();

  constructor(private elementRef: ElementRef) {
    super();
  }

  ngAfterViewInit(): void {
    this.setStartValue();
    this.setEndValue();
    this.fillSlider();
    if (this.vertical) {
      this.elementRef.nativeElement.style.transform = "rotate(90deg)";
    }
  }

  ngOnInit(): void {
    this.componentId = this.id;
    if (this.startValue === undefined) {
      this.startValue = this.minValue;
    }
    if (this.endValue === undefined) {
      this.endValue = this.maxValue;
    }
  }

  onStartChange(event: number) {
    this.startValue = event;
    switch (this.sliderBehavior) {
      case "push":
      default:
        if (this.startValue >= this.endValue) {
          if (this.endValue < this.maxValue) {
            this.endValue = this.endValue + 1;
            this.onEndChange(this.endValue);
          }
          this.startValue = this.endValue - 1;
        }
        this.startChange.emit(this.startValue);
        this.setStartValue();
        this.fillSlider();
        break;
      case "stop":
        if (this.startValue >= this.endValue) {
          this.startValue = this.endValue - 1;
        }
        this.startChange.emit(event);
        this.setStartValue();
        this.fillSlider();
        break;
      case "through":
        this.startChange.emit(this.startValue);
        this.setStartValue();
        this.fillSlider();
        break;
    }
  }

  onEndChange(event: number) {
    this.endValue = event;
    switch (this.sliderBehavior) {
      case "push":
      default:
        if (this.endValue <= this.startValue) {
          if (this.startValue > this.minValue) {
            this.startValue = this.startValue - 1;
            this.onStartChange(this.startValue);
          }
          this.endValue = this.startValue + 1;
        }
        this.endChange.emit(this.endValue);
        this.setEndValue();
        this.fillSlider();
        break;
      case "stop":
        if (this.endValue <= this.startValue) {
          this.endValue = this.startValue + 1;
        }
        this.endChange.emit(event);
        this.setEndValue();
        this.fillSlider();
        break;
      case "through":
        this.endChange.emit(this.startValue);
        this.setEndValue();
        this.fillSlider();
        break;
    }
  }

  setStartValue() {
    const rangeV = this.elementRef.nativeElement.querySelector(
      `#${this.id}rangeStartV`
    );
    const newValue = Number(
      ((this.startValue - this.minValue) / (this.maxValue - this.minValue)) *
      100
    );
    const newPosition = 8 - newValue * 0.15;
    rangeV.innerHTML = `<span>${this.startValue}</span>`;
    rangeV.style.left = `calc(${newValue}% + (${newPosition}px))`;
  }

  setEndValue() {
    const rangeV = this.elementRef.nativeElement.querySelector(
      `#${this.id}rangeEndV`
    );
    const newValue = Number(
      ((this.endValue - this.minValue) / (this.maxValue - this.minValue)) * 100
    );
    const newPosition = 8 - newValue * 0.15;
    rangeV.innerHTML = `<span>${this.endValue}</span>`;
    rangeV.style.left = `calc(${newValue}% + (${newPosition}px))`;
  }

  fillSlider() {
    const startSlider = this.elementRef.nativeElement.querySelector(
      ".gem-dual-range-start"
    );
    const endSlider = this.elementRef.nativeElement.querySelector(
      ".gem-dual-range-end"
    );

    const rangeDistance = this.maxValue - this.minValue;
    const startPosition = this.startValue - this.minValue;
    const endPosition = this.endValue - this.minValue;

    const startColor = getComputedStyle(
      document.documentElement
    ).getPropertyValue("--slider-color");
    const rangeColor = getComputedStyle(
      document.documentElement
    ).getPropertyValue("--slider-range-color");

    // const startColor = "black"
    // const rangeColor = "blue"

    if (startPosition < endPosition) {
      // Start handle is before the end handle
      startSlider.style.background = `linear-gradient(
        to right,
        ${startColor} 0%,
        ${startColor} ${(startPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(startPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(endPosition / rangeDistance) * 100}%,
        ${startColor} ${(endPosition / rangeDistance) * 100}%,
        ${startColor} 100%
      )`;

      endSlider.style.background = `linear-gradient(
        to right,
        ${startColor} 0%,
        ${startColor} ${(startPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(startPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(endPosition / rangeDistance) * 100}%,
        ${startColor} ${(endPosition / rangeDistance) * 100}%,
        ${startColor} 100%
      )`;
    } else {
      // Start handle is after the end handle
      startSlider.style.background = `linear-gradient(
        to right,
        ${startColor} 0%,
        ${startColor} ${(endPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(endPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(startPosition / rangeDistance) * 100}%,
        ${startColor} ${(startPosition / rangeDistance) * 100}%,
        ${startColor} 100%
      )`;

      endSlider.style.background = `linear-gradient(
        to right,
        ${startColor} 0%,
        ${startColor} ${(endPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(endPosition / rangeDistance) * 100}%,
        ${rangeColor} ${(startPosition / rangeDistance) * 100}%,
        ${startColor} ${(startPosition / rangeDistance) * 100}%,
        ${startColor} 100%
      )`;
    }
  }
}
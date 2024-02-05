import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  OnInit,
  Output,
} from "@angular/core";
import { GemRangeInput as GemRangeInputTOP } from "./gem-range-input/gem-range-input.component-gen";

@Component({
  selector: "gem-range-input",
  templateUrl: "./gem-range-input.component.html",
})
export class GemRangeInput
  extends GemRangeInputTOP
  implements OnInit, AfterViewInit
{

  constructor(private elementRef: ElementRef) {
    super();
  }

  @HostBinding("attr.id") componentId: string;

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

  // @Input() labelText: string;

  // @Input() id: string;

  // @Input() minValue: number;

  // @Input() maxValue: number;

  // @Input() value: number;

  @Output() valueChange = new EventEmitter<number>();

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

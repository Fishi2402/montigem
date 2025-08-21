/* (c) https://github.com/MontiCore/monticore */
import {
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  Output,
  ViewChild,
  SimpleChanges,
  ViewChildren,
  QueryList,
  AfterViewInit,
  OnChanges,
  OnDestroy
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { config, GemRangeInputComponent } from '../../../../build/gui/mc/fenix/input/GemRangeInputComponent';
import { Marker } from '../../../../build/cd/mc/fenix/input/gemrangeinputtypes/Marker';
import { Subscription } from 'rxjs';

@Component({
  standalone: true,
  imports: [...config.imports, FormsModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemRangeInput extends GemRangeInputComponent implements AfterViewInit, OnChanges, OnDestroy {
  private firstTime: boolean = true;

  @HostBinding('attr.id')
  componentId!: string;

  @ViewChild('slider', { static: false })
  sliderEl!: ElementRef<HTMLInputElement>;

  @ViewChild('rangeVElement', { static: false })
  rangeVElement!: ElementRef<HTMLElement>;

  @ViewChildren('tickLabel', { read: ElementRef })
  tickLabelEls!: QueryList<ElementRef<HTMLElement>>;

  private tickLabelsSub?: Subscription;

  @Output()
  valueChange = new EventEmitter<number>();

  ngOnChanges(changes: SimpleChanges): void {
    if (
      typeof this.minValue === 'number' &&
      typeof this.maxValue === 'number'
    ) {
      this.firstTime = true;
      const initial = (this.maxValue + this.minValue) / 2;
      this.setValue(initial);
      this.valueChange.emit(initial);
    }
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.adjustVerticalLabelMargins());

    this.tickLabelsSub = this.tickLabelEls.changes.subscribe(() => {
      setTimeout(() => this.adjustVerticalLabelMargins());
    });
  }

  ngOnDestroy(): void {
    this.tickLabelsSub?.unsubscribe();
  }

  onValueChange(event: number) {
    if (event === undefined) { return; }
    this.value = event;
    this.setValue(this.value);
    this.valueChange.emit(this.value);
  }

  snapToTick(): void {
    if (!this.snapping) return;
    if (!this.ticks || this.ticks.length === 0) return;

    const tickValues = this.ticks.map((marker: Marker) => marker.getValue());
    tickValues.push(this.minValue, this.maxValue);

    let nearest = tickValues[0];
    let minDiff = Math.abs(this.value - nearest);
    for (const v of tickValues) {
      const diff = Math.abs(this.value - v);
      if (diff < minDiff) {
        minDiff = diff;
        nearest = v;
      }
    }

    this.setValue(nearest);
    this.valueChange.emit(this.value);
  }

  override setValue(v: number): void {
    super.setValue(v);
    if (!this.rangeVElement) return;

    if (!this.firstTime) {
      this.rangeVElement.nativeElement.innerHTML = `<span>${this.value}</span>`;
    } else {
      this.firstTime = false;
    }
    this.rangeVElement.nativeElement.style.left = `${this.getThumbPosPxFor(v) + 2}px`;
  }

  get sliderBottomLength(): string {
    return this.vertical ? '100%' : '2em';
  }

  getTickPosition(marker: Marker): string {
    return `${this.getThumbPosPxFor(marker.getValue()) + 2}px`;
  }

  get visibleTicks() {
    if (!this.ticks) return [];
    const markerMap = new Map<number, Marker>();
    for (const marker of this.ticks) {
      if (marker.getValue() >= this.minValue && marker.getValue() <= this.maxValue) {
        markerMap.set(marker.getValue(), marker);
      }
    }
    return Array.from(markerMap.values());
  }

  public getThumbPosPxFor(value: number): number {
    const input = this.sliderEl?.nativeElement;
    if (!input) return 0;

    const min = this.minValue;
    const max = this.maxValue;
    const v = Math.min(max, Math.max(min, value)); // clamp

    const rect = input.getBoundingClientRect();
    const trackPx = this.vertical ? rect.height : rect.width;

    const cs = getComputedStyle(input);
    const thumbSizePx = parseFloat(cs.getPropertyValue('--thumb-size')) || 16;

    const usable = Math.max(0, trackPx - thumbSizePx);

    const denom = (max - min) || 1;
    const t = (v - min) / denom;

    const posPx = (thumbSizePx / 2) + t * usable;
    return posPx;
  }

  private adjustVerticalLabelMargins(): void {
    if (!this.vertical) return;
    if (!this.tickLabelEls || this.tickLabelEls.length === 0) return;

    this.tickLabelEls.forEach(labelEl => {
      const el = labelEl.nativeElement;
      const text = el.innerText || '';
      const length = text.length;

      const style = getComputedStyle(el);
      const fontSizePx = parseFloat(style.fontSize) || 12; // Fallback: 12px

      const factor = 0.3;
      const margin = length * fontSizePx * factor;

      el.style.marginTop = `${margin}px`;
    });
  }
}

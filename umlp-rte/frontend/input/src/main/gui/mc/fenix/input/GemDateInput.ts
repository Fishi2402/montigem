/* (c) https://github.com/MontiCore/monticore */

import {
  AfterViewInit,
  Component,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { config, GemDateInputComponent } from '../../../../../../build/gui2java2ts/mc/fenix/input/GemDateInputComponent';

import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.min.css';
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";

@Component({
  standalone: true,
  imports: [...config.imports, MatIconModule, MatTooltipModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemDateInput extends GemDateInputComponent implements AfterViewInit, OnDestroy, OnInit {

  @ViewChild('dateInput', { static: false }) dateInput!: ElementRef<HTMLInputElement>;
  private pickerElement: HTMLInputElement;

  private static readonly DATE_FORMAT = "d.m.Y";
  private static readonly TIME_FORMAT = "H:i";

  private picker: flatpickr.Instance | null = null;
  private _pickerEntry: Date;

  dateTimeEnabled: boolean = false;
  timeOnlyEnabled: boolean = false;

  override _id: string = Math.random() + '';
  override _labelText: string = "";
  override _disabled: boolean = false;

  @HostBinding("attr.id")
  componentId: string;

  @HostBinding("style.width")
  _width: string = '100%';

  @HostBinding("style.height")
  _height: string = '100%';

  @Input()
  override set disabled(isDisabled: boolean) {
    if (this.dateInput && isDisabled) {
      this.dateInput.nativeElement.disabled = isDisabled;
    }
  }

  get pickerEntry() {
    return this._pickerEntry;
  }

  @Input()
  override set entry(date: Date) {
    this._pickerEntry = date;
    if (this.picker) {
      this.picker.setDate(date);
    }
  }

  @Input()
  override set showDateTime(dateTimeEnabled: boolean) {
    this.dateTimeEnabled = dateTimeEnabled;
    this.reinitPicker();
  }

  @Input()
  override set showTimeOnly(timeOnlyEnabled: boolean) {
    this.timeOnlyEnabled = timeOnlyEnabled;
    this.reinitPicker();
  }

  ngOnInit(): void {
    this.componentId = this.id;
  }

  ngAfterViewInit(): void {
    this.initPicker();
  }

  ngOnDestroy() {
    this.picker?.destroy();
  }

  private initPicker() {
    this.pickerElement = this.dateInput.nativeElement;
    this.createPicker();
  }

  private reinitPicker() {
    if (!this.dateInput) return;
    this.picker?.destroy();
    this.createPicker();
  }

  private createPicker() {
    this.picker = flatpickr(this.pickerElement, {
      enableTime: this.timeOnlyEnabled || this.dateTimeEnabled,
      noCalendar: this.timeOnlyEnabled,
      dateFormat: this.getFormat(),
      time_24hr: true,
      allowInput: true,
      onChange: ([date]) => {
        this.entryChange.emit(date ?? null);
      },
      onClose: () => {
        this.picker?.setDate(this.pickerElement.value, true);
      }
    });
    this.picker.setDate(this.pickerEntry);
  }

  private getFormat(): string {
    if (!this.timeOnlyEnabled && this.dateTimeEnabled) {
      return `${GemDateInput.DATE_FORMAT} ${GemDateInput.TIME_FORMAT}`
    }
    else if (this.timeOnlyEnabled) {
      return GemDateInput.TIME_FORMAT;
    }
    return GemDateInput.DATE_FORMAT;
  }

}

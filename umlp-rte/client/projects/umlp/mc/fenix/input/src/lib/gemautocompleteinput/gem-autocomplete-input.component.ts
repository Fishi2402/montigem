import {
  Component, ElementRef,
  EventEmitter,
  HostBinding, HostListener,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges,
} from "@angular/core";
import { Option } from "../gemdropdowninput";
import { GemTextInputValidity } from "../gemtextinput";

@Component({
  selector: "gem-autocomplete-input",
  templateUrl: "./gem-autocomplete-input.component.html",
})
export class GemAutocompleteInput implements OnInit, OnChanges {
  @HostBinding("attr.id")
  @Input() id: string;

  @HostBinding("style.width")
  @Input() width: string = '100%';

  @HostBinding("style.height")
  @Input() height: string = '100%';

  @HostListener('document:click', ['$event'])
  clickOutside(event: any): void {
    if (!this.componentRef.nativeElement.contains(event.target)) {
      this.hideDropdown();
    }
  }

  @HostListener('document:keydown', ['$event'])
  enterOutside(event: any): void {
    if (
      (event.key === "Enter" && !this.componentRef.nativeElement.contains(event.target)) ||
      event.key === "Tab"
    ) {
      this.hideDropdown();
    }
  }

  @Input() labelText: string;
  @Input() entry: string;
  @Input() disabled: boolean = false;
  @Input() options: Option[];
  @Input() validity: GemTextInputValidity;
  @Input() placeholder: string = "";

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() public keyPress: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;
  @Output() public keyRelease: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;

  filteredOptions: Option[] = [];
  isContentShown: boolean = false;
  highlightIdx: number = -1;

  constructor(private componentRef: ElementRef) {}

  ngOnInit(): void {
    this.filteredOptions = this.options;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["options"]) {
      this.filteredOptions = changes["options"].currentValue;
    }
  }

  onEntryChange(entry: string): void {
    this.selectOption(entry);
    this.showDropdown();
  }

  onKeyPress(event: KeyboardEvent): void {
    this.keyPress.emit(event);
  }

  onKeyRelease(event: KeyboardEvent): void {
    this.keyRelease.emit(event);
  }

  showDropdown(): void {
    if (!this.isContentShown && this.filteredOptions.length > 0) {
      this.isContentShown = true;
    }
  }

  selectOption(entry: string): void {
    this.entry = entry;
    this.entryChange.emit(this.entry);
    this.filterOptions();
    this.hideDropdown();
  }

  selectWithEnter(): void {
    if (this.highlightIdx < 0) {
      this.showDropdown();
      return;
    }
    this.selectOption(this.filteredOptions[this.highlightIdx].value);
  }

  focusDown(): void {
    const newIdx = this.highlightIdx + 1;
    this.highlightIdx = newIdx < this.filteredOptions.length ? newIdx : this.filteredOptions.length - 1;
    if (this.isOverflown()) {
      this.automaticScrollDown();
    }
  }

  focusUp(): void {
    const newIdx = this.highlightIdx - 1;
    this.highlightIdx = newIdx >= -1 ? newIdx : -1;
    if (this.isOverflown()) {
      this.automaticScrollUp();
    }
  }

  setHighlightIdx(idx: number): void {
    if (idx != this.highlightIdx) {
      this.highlightIdx = idx;
    }
  }

  toggleDropdown(): void {
    if (!this.isContentShown && this.filteredOptions.length > 0) {
      this.showDropdown();
    }
    else {
      this.hideDropdown();
    }
  }

  getOptionsId(): string {
    return this.componentRef.nativeElement.getAttribute('class') + "Options";
  }

  private filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.value.toUpperCase().indexOf(this.entry.toUpperCase()) > -1
    );
    if (this.filteredOptions.length == 1) {
      this.highlightIdx = 0;
    }
    else {
      this.resetHighlightIdx();
    }
  }

  private hideDropdown(): void {
    this.isContentShown = false;
    this.resetHighlightIdx();
  }

  private resetHighlightIdx() {
    this.highlightIdx = -1;
  }

  private isOverflown(): boolean {
    const element: HTMLElement = document.getElementById(this.getOptionsId());
    return element.scrollHeight > element.clientHeight;
  }

  private automaticScrollDown(): void {
    const element: HTMLElement = document.getElementById(this.getOptionsId());
    const highlightOption: HTMLElement = element.querySelector(".highlight-option");
    const optionHeight: number = highlightOption?.offsetHeight;
    if (highlightOption?.offsetTop + optionHeight >= element.clientHeight) {
      element.scrollTop += optionHeight;
    }
  }

  private automaticScrollUp(): void {
    const element: HTMLElement = document.getElementById(this.getOptionsId());
    const highlightOption: HTMLElement = element.querySelector(".highlight-option");
    const optionHeight: number = highlightOption?.offsetHeight;
    if (highlightOption?.offsetTop - optionHeight < element.scrollTop) {
      element.scrollTop -= optionHeight;
    }
  }

}

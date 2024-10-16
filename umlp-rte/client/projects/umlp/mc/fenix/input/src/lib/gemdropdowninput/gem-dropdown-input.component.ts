import {
  ChangeDetectorRef,
  Component, ElementRef,
  EventEmitter,
  HostBinding, HostListener,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from "@angular/core";
import {GemTextInputValidity} from "../gemtextinput";
import {Option} from "./gem-dropdown-input-types/gem-dropdown-input.types";

@Component({
  selector: "gem-dropdown-input",
  templateUrl: "./gem-dropdown-input.component.html",
})
export class GemDropdownInput implements OnInit, OnChanges {
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
    if (event.key === "Enter" && !this.componentRef.nativeElement.contains(event.target)) {
      this.hideDropdown();
    }
  }

  @Input() labelText: string;
  @Input() entry: string = "";
  @Input() disabled: boolean = false;
  @Input() options: Option[];
  @Input() validity: GemTextInputValidity;
  @Input() searchEnabled: boolean = false;

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() public keyPress: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;
  @Output() public keyRelease: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;

  elementId: string = Math.random() + '';
  isContentShown: boolean = false;
  filterString: string = "";
  filteredOptions: Option[] = [];
  highlightIdx: number = -1;

  constructor(private componentRef: ElementRef) {}

  ngOnInit(): void {
    this.filteredOptions = this.options;
    if (this.filteredOptions.length > 0) {
      this.selectOption(this.filteredOptions[0]);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["options"]) {
      this.filteredOptions = changes["options"].currentValue;
    }
  }

  onKeyPress(event: KeyboardEvent): void {
    this.keyPress.emit(event);
  }

  onKeyRelease(event: KeyboardEvent): void {
    this.keyRelease.emit(event);
  }

  toggleDropdown(): void {
    if (!this.isContentShown && this.filteredOptions.length > 0) {
      this.showDropdown();
    }
    else {
      this.hideDropdown();
    }
  }

  selectOption(option: Option): void {
    this.entry = option.value;
    this.entryChange.emit(this.entry);
    this.hideDropdown();
  }

  selectWithEnter(event: any): void {
    event.preventDefault();
    if (this.highlightIdx < 0) {
      this.toggleDropdown();
      return;
    }
    this.selectOption(this.filteredOptions[this.highlightIdx]);
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

  filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.value.toUpperCase().indexOf(this.filterString.toUpperCase()) > -1
    );
  }

  getInputId(): string {
    return this.elementId + "Input";
  }

  getSearchInputId(): string {
    return this.elementId + "Search";
  }

  getOptionsId(): string {
    return this.elementId + "Options";
  }

  hideDropdown(): void {
    this.isContentShown = false;
    this.highlightIdx = -1;
    this.filterString = "";
    this.filteredOptions = this.options;
  }

  tabOut(): void {
    if (this.isContentShown) {
      this.hideDropdown();
    }
  }

  private showDropdown(): void {
    this.isContentShown = true;
  }

  private isOverflown(): boolean {
    const element: HTMLElement = document.getElementById(this.getOptionsId());
    return element.scrollHeight > element.clientHeight;
  }

  private automaticScrollDown(): void {
    const element: HTMLElement = document.getElementById(this.getOptionsId());
    const highlightOption: HTMLElement = element.querySelector(".highlight-option");
    const optionHeight: number = highlightOption?.offsetHeight;
    const optionOffset: number = this.searchEnabled ? 0 : optionHeight;
    if (highlightOption?.offsetTop + optionOffset >= element.clientHeight) {
      element.scrollTop += optionHeight;
    }
  }

  private automaticScrollUp(): void {
    const element: HTMLElement = document.getElementById(this.getOptionsId());
    const highlightOption: HTMLElement = element.querySelector(".highlight-option");
    const optionHeight: number = highlightOption?.offsetHeight;
    const optionOffset: number = this.searchEnabled ? optionHeight * 2 : optionHeight;
    if (highlightOption?.offsetTop - optionOffset < element.scrollTop) {
      element.scrollTop -= optionHeight;
    }
  }

}

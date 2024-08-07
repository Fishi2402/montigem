import {
  Component,
  EventEmitter,
  HostBinding,
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

  @Input() labelText: string;
  @Input() entry: string;
  @Input() disabled: boolean = false;
  @Input() options: Option[];
  @Input() validity: GemTextInputValidity;
  @Input() placeholder: string;

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() public keyPress: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;
  @Output() public keyRelease: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>;

  filteredOptions: Option[] = [];
  isContentShown: boolean = false;
  highlightIdx: number = -1;

  ngOnInit(): void {
    this.filteredOptions = this.options;
    window.onclick = (event) => {
      if (
        !event.target.matches(".gem-text-input") &&
        !event.target.matches(".mat-icon") &&
        !event.target.matches(".gem-dropdown-input") &&
        this.isContentShown
      ) {
        this.hideDropdown();
      }
    }
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
  }

  selectWithEnter(): void {
    if (this.highlightIdx < 0) {
      this.showDropdown();
      return;
    }
    this.selectOption(this.filteredOptions[this.highlightIdx].value);
    this.hideDropdown();
  }

  focusDown(): void {
    const newIdx = this.highlightIdx + 1;
    this.highlightIdx = newIdx < this.filteredOptions.length ? newIdx : this.filteredOptions.length - 1;
  }

  focusUp(): void {
    const newIdx = this.highlightIdx - 1;
    this.highlightIdx = newIdx >= -1 ? newIdx : -1;
  }

  setHighlightIdx(idx: number): void {
    this.highlightIdx = idx;
  }

  private filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.value.toUpperCase().indexOf(this.entry.toUpperCase()) > -1
    );
  }

  toggleDropdown(): void {
    if (!this.isContentShown && this.filteredOptions.length > 0) {
      this.showDropdown();
    }
    else {
      this.hideDropdown();
    }
  }

  private hideDropdown(): void {
    this.isContentShown = false;
    this.resetHighlightIdx();
  }

  private resetHighlightIdx() {
    this.highlightIdx = -1;
  }

}

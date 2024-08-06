import {
  Component, ElementRef,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output, QueryList, ViewChildren,
} from "@angular/core";
import { Option } from "../gemdropdowninput";
import { GemTextInputValidity } from "../gemtextinput";

@Component({
  selector: "gem-autocomplete-input",
  templateUrl: "./gem-autocomplete-input.component.html",
})
export class GemAutocompleteInput implements OnInit {
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

  onEntryChange(entry: string): void {
    this.entry = entry;
    this.entryChange.emit(this.entry);
    this.filterOptions();
    this.showDropdown();
  }

  showDropdown(): void {
    if (!this.isContentShown && this.filteredOptions.length > 0) {
      this.isContentShown = true;
    }
  }

  selectOption(option: Option): void {
    this.entry = option.value;
    this.entryChange.emit(this.entry);
  }

  selectWithEnter() {
    this.selectOption(this.filteredOptions[this.highlightIdx]);
    this.hideDropdown();
  }

  focusDown(): void {
    const newIdx = this.highlightIdx + 1;
    this.highlightIdx = newIdx < this.filteredOptions.length ? newIdx : this.filteredOptions.length - 1;
  }

  focusUp(): void {
    const newIdx = this.highlightIdx - 1;
    this.highlightIdx = newIdx >= 0 ? newIdx : 0;
  }

  setHighlightIdx(idx: number): void {
    this.highlightIdx = idx;
  }

  private filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.value.toUpperCase().indexOf(this.entry.toUpperCase()) > -1
    );
  }

  private hideDropdown(): void {
    this.isContentShown = false;
  }

}

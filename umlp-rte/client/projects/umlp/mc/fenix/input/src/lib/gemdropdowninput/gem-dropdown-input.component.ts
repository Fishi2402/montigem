import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output
} from "@angular/core";
import {GemTextInputValidity} from "../gemtextinput";
import {Option} from "./gem-dropdown-input-types/gem-dropdown-input.types";

@Component({
  selector: "gem-dropdown-input",
  templateUrl: "./gem-dropdown-input.component.html",
})
export class GemDropdownInput implements OnInit {
  @HostBinding("attr.id")
  @Input() id: string;

  @HostBinding("style.width")
  @Input() width: string = '100%';

  @HostBinding("style.height")
  @Input() height: string = '100%';

  @Input() labelText: string;
  @Input() entry: string = "";
  @Input() disabled: boolean = false;
  @Input() options: Option[];
  @Input() validity: GemTextInputValidity;
  @Input() searchEnabled: boolean;

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();

  isContentShown: boolean = false;
  filterString: string = "";
  filteredOptions: Option[] = [];
  highlightIdx: number = -1;

  constructor(private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.filteredOptions = this.options;
    window.onclick = (event) => {
      if (
        !event.target.matches(".gem-text-input") &&
        !event.target.matches(".mat-icon") &&
        !event.target.matches(".gem-dropdown-input") &&
        !event.target.matches(".gem-dropdown-button") &&
        this.isContentShown
      ) {
        this.hideDropdown();
      }
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

  selectOption(option: Option): void {
    this.entry = option.value;
    this.entryChange.emit(this.entry);
  }

  selectWithEnter(event: any): void {
    event.preventDefault();
    if (this.highlightIdx < 0) {
      this.toggleDropdown();
      return;
    }
    this.selectOption(this.filteredOptions[this.highlightIdx]);
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

  filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.value.toUpperCase().indexOf(this.filterString.toUpperCase()) > -1
    );
  }

  getInputId(): string {
    return this.id + "Input"
  }

  private showDropdown(): void {
    this.isContentShown = true;
    this.toggleFocus();
  }

  private hideDropdown(): void {
    this.isContentShown = false;
    this.resetHighlightIdx();
    this.toggleFocus();
  }

  private resetHighlightIdx() {
    this.highlightIdx = -1;
  }

  private toggleFocus(): void {
    if (!this.searchEnabled) {
      return;
    }
    this.cdRef.detectChanges();
    if (this.isContentShown) {
      document.getElementById("searchInput").focus();
    }
    else {
      document.getElementById(this.getInputId()).focus();
    }
  }

}

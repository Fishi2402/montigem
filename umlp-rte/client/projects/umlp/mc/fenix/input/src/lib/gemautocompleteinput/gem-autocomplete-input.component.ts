import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
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

  isContentShown: boolean = false;

  ngOnInit(): void {
    window.onclick = (event) => {
      if (
        !event.target.matches(".gem-text-input") &&
        !event.target.matches(".mat-icon") &&
        !event.target.matches(".gem-dropdown-input") &&
        this.isContentShown
      ) {
        console.log("Click outside content", event.target);
        this.hideDropdown();
      }
    }
  }

  onEntryChange(entry: string): void {
    this.entry = entry;
    this.entryChange.emit(this.entry);
    this.filterOptions();
  }

  showDropdown(): void {
    if (!this.isContentShown && this.options.length > 0) {
      this.isContentShown = true;
    }
  }

  selectOption(option: Option): void {
    this.entry = option.value;
    this.entryChange.emit(this.entry);
  }

  private filterOptions(): void {
    const dropdownOptions=
      document.getElementsByClassName("gem-dropdown-option") as HTMLCollectionOf<HTMLElement>;
    for (let i = 0; i < dropdownOptions.length; i++) {
      const textValue: string = dropdownOptions[i].textContent || dropdownOptions[i].innerText;
      if (textValue.toUpperCase().indexOf(this.entry.toUpperCase()) > -1) {
        dropdownOptions[i].style.display = "block";
      } else {
        dropdownOptions[i].style.display = "none";
      }
    }
  }

  private hideDropdown(): void {
    this.isContentShown = false;
  }

}

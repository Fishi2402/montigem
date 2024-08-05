import {
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
  @Input() options: Option[] = [];
  @Input() validity: GemTextInputValidity;
  @Input() placeholder: string;
  @Input() searchEnabled: boolean;

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();

  isContentShown: boolean = false;
  filterString: string = "";

  ngOnInit(): void {
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

  toggleDropdown(): void {
    if (!this.isContentShown && this.options.length > 0) {
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

  filterOptions(): void {
    const dropdownOptions=
      document.getElementsByClassName("gem-dropdown-option") as HTMLCollectionOf<HTMLElement>;
    for (let i = 0; i < dropdownOptions.length; i++) {
      const textValue: string = dropdownOptions[i].textContent || dropdownOptions[i].innerText;
      if (textValue.toUpperCase().indexOf(this.filterString.toUpperCase()) > -1) {
        dropdownOptions[i].style.display = "block";
      } else {
        dropdownOptions[i].style.display = "none";
      }
    }
  }

  private showDropdown(): void {
    this.isContentShown = true;
  }

  private hideDropdown(): void {
    this.isContentShown = false;
  }

}

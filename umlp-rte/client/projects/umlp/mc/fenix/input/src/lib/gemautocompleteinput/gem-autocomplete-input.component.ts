import {
  Component, ElementRef,
  EventEmitter,
  HostBinding,
  Input, OnInit,
  Output, ViewChild,
} from "@angular/core";
import { Option } from "../gemdropdowninput";
import { GemTextInputValidity } from "../gemtextinput";

@Component({
  selector: "gem-autocomplete-input",
  templateUrl: "./gem-autocomplete-input.component.html",
  styleUrl: "./gem-autocomplete-input.component.scss"
})
export class GemAutocompleteInput implements OnInit {
  @HostBinding("attr.id")
  @Input() id: string;

  @HostBinding("style.width")
  @Input() width: string = '100%';

  @HostBinding("style.height")
  @Input() height: string = '100%';

  @ViewChild("content") content: ElementRef;

  @Input() labelText: string;
  @Input() entry: string;
  @Input() disabled: boolean = false;
  @Input() options: Option[];
  @Input() validity: GemTextInputValidity;
  @Input() placeholder: string;

  @Output() public entryChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() public keyRelease: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>();
  @Output() public keyPress: EventEmitter<KeyboardEvent> = new EventEmitter<KeyboardEvent>();

  private isContentShown: boolean = false;

  ngOnInit(): void {
    window.onclick = (event) => {
      if (!event.target.matches(".gem-dropdown-button") && this.isContentShown) {
        this.hideDropdown();
      }
    }
  }

  onEntryChange(event: string): void {

    this.entryChange.emit(event);
  }

  private showDropdown(): void {
    console.log("Showing dropdown");
    this.isContentShown = true;
    this.content.nativeElement.style.display = "block";
  }

  private hideDropdown(): void {
    console.log("Hide dropdown");
    this.isContentShown = false;
    this.content.nativeElement.style.display = "none";
  }

  toggleDropdown(): void {
    if (!this.isContentShown && this.options.length > 0) {
      this.showDropdown();
    }
    else {
      this.hideDropdown();
    }
  }

  selectOption(): void {

  }


}

import {
  Component,
  Input,
} from "@angular/core";

@Component({
  selector: "gem-icon",
  templateUrl: "./gem-icon.component.html",
  styleUrl: "./gem-icon.component.scss"
})
export class GemIcon {
  @Input() name: string;
  @Input() color: string;
  @Input() size: string;
}

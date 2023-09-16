import { Attribute, Directive, HostBinding, Input } from "@angular/core";

@Directive({
  selector: "input",
})
export class InputModeDirective {
  @Input()
  @HostBinding("attr.inputmode")
  inputmode;

  @Attribute("pattern") private pattern: string

  @HostBinding("attr.pattern")
  get patternAttribute(): string {
    return (this.inputmode === "numeric") && !this.pattern
      ? "[0-9 ]+"
      : this.pattern;
  }
}

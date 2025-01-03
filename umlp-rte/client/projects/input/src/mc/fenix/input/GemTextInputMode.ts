/* (c) https://github.com/MontiCore/monticore */

import { Attribute, Directive, HostBinding, Input } from "@angular/core";

@Directive({
  standalone: true,
  selector: "[gemInputMode]",
})
export class GemTextInputMode {
  @Input()
  gemInputMode;

  @Attribute("pattern")
  private pattern: string

  @HostBinding("attr.pattern")
  get patternAttribute(): string {
    return (this.gemInputMode === "numeric") && !this.pattern
      ? "[0-9 ]+"
      : this.pattern;
  }
}

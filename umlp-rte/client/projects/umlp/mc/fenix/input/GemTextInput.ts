/* (c) https://github.com/MontiCore/monticore */

import { Component, EventEmitter, HostBinding, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { config, GemTextInputComponent } from '@src/mc/fenix/input/GemTextInputComponent';
import { GemTextInputMode } from './GemTextInputMode';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  standalone: true,
  imports: [...config.imports, FormsModule, GemTextInputMode, MatTooltipModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemTextInput extends GemTextInputComponent {
  isNumeric: boolean = false;

  @HostBinding("attr.id")
  componentId: string;

  @HostBinding("style.width")
  _width: string = '100%';

  @HostBinding("style.height")
  _height: string = '100%';

  @Output() public entryChange = new EventEmitter<string>();
  @Output() public override keyRelease = new EventEmitter<KeyboardEvent>();
  @Output() public override keyPress = new EventEmitter<KeyboardEvent>();

  override get inputType(): string {
    return this._inputType;
  }

  @Input()
  override set inputType(inputType: string) {
    if (inputType === "numericText") {
      this._inputType = "text";
      this.isNumeric = true;
    } else {
      this._inputType = inputType;
    }
  }

  onEntryChange(event: string) {
    this.entryChange.emit(event);
  }

  ngOnInit(): void {
    this.componentId = this.id;
  }
}
/* (c) https://github.com/MontiCore/monticore */

import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { config, GemRadioInputComponent } from '../../../../build/gui/mc/fenix/input/GemRadioInputComponent';

@Component({
  standalone: true,
  imports: [...config.imports, FormsModule],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class GemRadioInput extends GemRadioInputComponent {

  rightPosition: boolean = true;

  @Output()
  selectedValueChange = new EventEmitter<string>();

  onSelectedValueChange(event: string) {
    this.selectedValueChange.emit(event);
  }

}
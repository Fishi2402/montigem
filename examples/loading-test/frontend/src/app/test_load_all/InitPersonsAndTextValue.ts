import { Component } from '@angular/core';
import { InitPersonsAndTextValueComponent, config } from 'test_load_all/InitPersonsAndTextValueComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles
})
export class InitPersonsAndTextValue extends InitPersonsAndTextValueComponent {
  myPersons = [];

  override getPersons() {
    return this.myPersons;
  }
}
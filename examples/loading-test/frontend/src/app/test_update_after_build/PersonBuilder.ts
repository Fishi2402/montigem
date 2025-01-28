/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { PersonBuilderComponent,config } from 'test_update_after_build/PersonBuilderComponent';


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class PersonBuilder extends PersonBuilderComponent {
  public override buildPerson() {
    return () => {
      // Default Age
      this.getPerson().setAge(42)
      this.getPerson().build()
    }
  }
}

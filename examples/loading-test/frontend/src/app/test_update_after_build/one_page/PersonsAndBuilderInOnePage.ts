/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { PersonsAndBuilderInOnePageComponent,config } from 'test_update_after_build/one_page/PersonsAndBuilderInOnePageComponent';


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class PersonsAndBuilderInOnePage extends PersonsAndBuilderInOnePageComponent {
  public override buildPerson() {
    return () => {
      // Default Age
      this.getPerson().setAge(42)
      this.getPerson().build()
    }
  }
}

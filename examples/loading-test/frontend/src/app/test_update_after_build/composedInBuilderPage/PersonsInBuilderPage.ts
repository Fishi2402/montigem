/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { PersonsInBuilderPageComponent,config } from '@src/test_update_after_build/composed_in_builder_page/PersonsInBuilderPageComponent';


@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class PersonsInBuilderPage extends PersonsInBuilderPageComponent {
  public override buildPerson() {
    return () => {
      // Default Age
      this.getPerson().setAge(42)
      this.getPerson().build()
    }
  }
}

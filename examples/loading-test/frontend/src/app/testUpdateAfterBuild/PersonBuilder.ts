/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { PersonBuilderComponent,config } from '@src/testUpdateAfterBuild/PersonBuilderComponent';


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

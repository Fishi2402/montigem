/* (c) https://github.com/MontiCore/monticore */

import { CreatePersonComponent, config } from '@src/person/CreatePersonComponent';
import { Component } from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class CreatePerson extends CreatePersonComponent {

  public override getBdayEntry(): string {
    return this.getPerson()?.getBirthday()?.toDateString();
  }

  public override createButtonLeftClick(): void {
    this.getPerson().setAddress1(this.getAddress());
    this.getPerson().setAboutMe("");
    super.createButtonLeftClick();
  }

  foo() {
    alert('Created person')
  }

}

/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { AnimalCreateComponent,config } from 'test_update_of_builder/AnimalCreateComponent';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
})
export class AnimalCreate extends AnimalCreateComponent {


}

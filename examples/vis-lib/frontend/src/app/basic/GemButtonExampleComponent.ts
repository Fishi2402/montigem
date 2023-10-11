/* (c) https://github.com/MontiCore/monticore */

import { GemButtonExampleComponentTOP } from '@src/basic/GemButtonExampleComponentTOP';
import { Component } from '@angular/core';

@Component({
  selector: 'gembuttoexample',
  templateUrl: '../../../build/frontendGen/build/appgui/basic/GemButtonExample.html'
  //styleUrls: ['./Create.scss']
})
export class GemButtonExampleComponent extends GemButtonExampleComponentTOP {



  public override alert(): void {
    alert("Hello");
  }

}

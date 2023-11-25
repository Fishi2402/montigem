
import { GemButtonExampleComponentTOP } from '@src/example_basic/GemButtonExampleComponentTOP';
import { Component } from '@angular/core';
@Component({
  selector: 'GemButtonExample',
  templateUrl: '../../../build/frontendGen/build/appgui/example_basic/GemButtonExample.html',
  //styleUrls: ['./button.component.css']
})

export  class GemButtonExampleComponent extends GemButtonExampleComponentTOP  {
/* Hookpoint: ClassContent:Elements */

  gemButton0LeftClick(event: MouseEvent): void {
    alert('Hello')
  }
}

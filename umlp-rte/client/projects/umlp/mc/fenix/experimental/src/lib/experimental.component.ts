import { Component } from '@angular/core';
import { CompositeComponent } from "@umlp/composite";

@Component({
  selector: 'gem-experimental',
  standalone: true,
  imports: [CompositeComponent],
  template: `
    <gem-composite>
      experimental works!
    </gem-composite>
  `,
  styles: ``
})
export class ExperimentalComponent {

}

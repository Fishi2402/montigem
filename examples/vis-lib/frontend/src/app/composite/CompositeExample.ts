import { Component } from '@angular/core';
import { CompositeComponent } from '@umlp/composite';

@Component({
  selector: 'composite-example',
  standalone: true,
  imports: [CompositeComponent],
  template: `
    <gem-composite [title]="'heh'">
    </gem-composite>
  `,
  styles: ``
})
export class CompositeExample {
}

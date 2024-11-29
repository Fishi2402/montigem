import { Component } from '@angular/core';
import { ExperimentalComponent } from '@umlp/experimental';

@Component({
  selector: 'experimental-example',
  standalone: true,
  imports: [ExperimentalComponent],
  template: `
    <gem-experimental [title]="'heh'">
    </gem-experimental>
  `,
  styles: ``
})
export class ExperimentalExample {
}

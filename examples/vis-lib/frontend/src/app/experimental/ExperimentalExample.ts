import { Component } from '@angular/core';
import { foo } from '@umlp/experimental';

@Component({
  selector: 'experimental-example',
  standalone: true,
  template: `
    {{ test }}
  `,
  styles: ``
})
export class ExperimentalExample {
  test = foo
}

import { Component } from '@angular/core';
import { GemcardModule } from '@umlp/arrange';

@Component({
  selector: 'gem-composite',
  standalone: true,
  imports: [GemcardModule],
  template: `
    <gem-card [title]="'heh'">
    </gem-card>
  `,
  styles: ``
})
export class CompositeComponent {

}

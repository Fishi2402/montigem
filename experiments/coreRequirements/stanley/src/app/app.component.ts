/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { PersonCreate } from './stanley/personcreate/person-create/person-create.component';
import { PersonOverview } from './stanley/personoverview/person-overview/person-overview.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'stanley';

  readonly navigation = [
    {url: PersonCreate.url, title: PersonCreate.title},
    {url: PersonOverview.url, title: PersonOverview.title}
  ];
}

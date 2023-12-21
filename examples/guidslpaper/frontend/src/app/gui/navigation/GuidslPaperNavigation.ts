/* (c) https://github.com/MontiCore/monticore */

import {Person} from '@src/guidslpaper/Person';
import {GuidslPaperNavigationComponent} from '@src/gui/navigation/GuidslPaperNavigationComponent';
import {Component} from '@angular/core';

@Component({
  selector: 'guidsl-paper-navigation',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/navigation/GuidslPaperNavigation.html',
  styleUrls: ["./GuidslPaperNavigation.scss"]
})

export class GuidslPaperNavigation extends GuidslPaperNavigationComponent {

  public getPersonNavTarget(p: Person): string {
    return "/gui/personcreate/" + p.getGemId();
  }

}



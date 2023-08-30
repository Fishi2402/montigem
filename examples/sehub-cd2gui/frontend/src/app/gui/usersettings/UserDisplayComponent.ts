/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { UserDisplayComponentTOP } from '@src/gui/usersettings/UserDisplayComponentTOP';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'user-display',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/usersettings/UserDisplay.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./UserDisplay.scss',]
})
export class UserDisplayComponent extends UserDisplayComponentTOP {

}

/* (c) https://github.com/MontiCore/monticore */

import {Component} from '@angular/core';
import {KitchenPageComponent} from '@src/gui/kitchen/KitchenPageComponent';
import {GemList} from '@umlp/common';

import {KitchenCompanionManager} from '@src/kitchencompanion/KitchenCompanionManager';
import {User} from '@src/kitchencompanion/User';


@Component({
  selector: 'kitchen-page',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/kitchen/KitchenPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['./KitchenPage.scss',]
})
export class KitchenPage extends KitchenPageComponent  {

  public ngOnInit(): void {

    KitchenCompanionManager.getUserList().then(gemPromiseRes => {
      this.users = gemPromiseRes;
    });

    super.ngOnInit();
  }


}



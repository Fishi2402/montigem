/* (c) https://github.com/MontiCore/monticore */
import { BatteryPackOverviewComponent ,config} from '@src/gui/pack/BatteryPackOverviewComponent';

import {
  Component
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export  class BatteryPackOverview extends BatteryPackOverviewComponent {

  public override editPack(): (arg: void) => void {
    return $ => this._router.navigate(["gui/pack/PackEditPage/"+this.getPack().getGemId()]);
  }

}



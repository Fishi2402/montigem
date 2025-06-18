/* (c) https://github.com/MontiCore/monticore */
import { TitleBarComponent, config} from 'gui/editprocessmodels/header/TitleBarComponent';

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

export class TitleBar extends TitleBarComponent {
  public override goToGraphicalView (): (arg: void) => void {
    requestAnimationFrame(() => {
      document.querySelector('.graphicalViewButton > button.gem-button').classList.add('clicked');
      document.querySelector('.textualViewButton > button.gem-button').classList.remove('clicked');
    });

    return $ => this._router.navigate(["gui/editprocessmodels/graphicalview/GraphicalView/" + this.getPm().getGemId()]);
  }

  public override goToTextualView (): (arg: void) => void {
    requestAnimationFrame(() => {
      document.querySelector('.textualViewButton > button.gem-button').classList.add('clicked');
      document.querySelector('.graphicalViewButton > button.gem-button').classList.remove('clicked');
    });

    return $ => this._router.navigate(["gui/editprocessmodels/textualview/TextualView/" + this.getPm().getGemId()]);
  }
}

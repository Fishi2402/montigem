/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding } from '@angular/core';
import { config, GemAudioComponent } from '../../../../build/gui/mc/fenix/basic/GemAudioComponent';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  styles: config.styles,
  templateUrl: config.templateUrl,
})
export class GemAudio extends GemAudioComponent {
  @HostBinding("style.width")
  _width: string = "auto";

  public ngOnInit(): void { }
}
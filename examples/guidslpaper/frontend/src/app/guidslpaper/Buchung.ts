/* (c) https://github.com/MontiCore/monticore */

import {BuchungTOP} from '@src/guidslpaper/BuchungTOP';

export class Buchung extends BuchungTOP {

  public getLfdeNummer(): string {
    return this.getBuchungsdatum().toString();
  }

}

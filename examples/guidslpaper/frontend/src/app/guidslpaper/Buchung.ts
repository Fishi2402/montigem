/* (c) https://github.com/MontiCore/monticore */

import {BuchungTOP} from 'guidslpaper/BuchungTOP';

export class Buchung extends BuchungTOP {

  public getLfdeNummer(): string {
    return this.getBuchungsdatum().toString();
  }

}

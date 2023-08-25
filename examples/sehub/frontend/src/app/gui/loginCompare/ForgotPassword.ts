/* (c) https://github.com/MontiCore/monticore */

import {SehubManager} from '@src/sehub/SehubManager';
import {GemList} from '@umlp/common';
import {User} from '@src/sehub/User';


export class ForgotPassword {

  protected users: GemList<User>

  public getUsers(): GemList<User> {
    return this.users;
  }

  public getGemCard0Title(): string {
    return "SE Hub";
  }

  public getGemColumn4HAlign(): string {
    return "center";
  }

  public getGemColumn4Width(): string {
    return "600px";
  }

  public getGemColumn4RowGap(): string {
    return "20px";
  }

  public getGemColumn1HAlign(): string {
    return "center";
  }

  public getGemColumn1Height(): string {
    return "auto";
  }

  public getGemColumn1Width(): string {
    return "800px";
  }

  public getGemColumn1RowGap(): string {
    return "105px";
  }

  public getGemColumn2HAlign(): string {
    return "center";
  }

  public getGemColumn2Width(): string {
    return "150px";
  }

  public getGemImage3Source(): string {
    return "assets/images/Eule.png";
  }
}



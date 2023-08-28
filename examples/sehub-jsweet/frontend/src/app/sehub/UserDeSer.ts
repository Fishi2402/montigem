/* (c) https://github.com/MontiCore/monticore */

import {UserDeSerTOP} from '@src/sehub/UserDeSerTOP';
import {SehubRole} from '@src/sehub/SehubRole';
import {User} from '@src/sehub/User';

export  class UserDeSer extends UserDeSerTOP {

  protected override serializeRole (obj: User, printer: any): void {
    printer["role"] = SehubRole[obj.getRole()];
  }
}

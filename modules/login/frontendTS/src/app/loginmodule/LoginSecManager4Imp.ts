/* (c) https://github.com/MontiCore/monticore */

import {LoginSecManager} from '@src/loginmodule/LoginSecManager';

import {ActivatedRoute, Router} from "@angular/router";

/*
This class exists to be able to be imported for the LoginFrontendPageNGOnInit template.
This avoids duplicate imports (which fail in TS)
 */
export class LoginSecManager4Imp {

  public static shouldAbortOnInit(router: Router, route?: ActivatedRoute): boolean {
    return LoginSecManager.getInstance()._shouldAbortOnInit(router, route);
  }


}



/* (c) https://github.com/MontiCore/monticore */

import {CommandDeSer} from '@umlp/common';
import {GetCurrentUserRequest} from "./GetCurrentUserRequest";

export  class GetCurrentUserRequestDeSer extends CommandDeSer<GetCurrentUserRequest>  {

  protected  fromJsonObject (jsonObject: any): GetCurrentUserRequest {
    return new GetCurrentUserRequest();
  }
}



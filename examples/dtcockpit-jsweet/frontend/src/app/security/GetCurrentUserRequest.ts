/* (c) https://github.com/MontiCore/monticore */
import {Request, SimpleResult} from '@umlp/common';

export class GetCurrentUserRequest extends Request<number>  {
  public constructor() {
    super("security.GetCurrentUserRequest");
  }

  public doRun (): Promise<SimpleResult<number>> {
    throw new Error("Can only be executed in the backend!")
  }
}



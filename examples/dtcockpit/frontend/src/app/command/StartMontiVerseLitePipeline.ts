/* (c) https://github.com/MontiCore/monticore */
import {Request, StatusResult} from '@umlp/common';
import {NotificationService} from '@umlp/common';

export class StartMontiVerseLitePipeline extends Request<void>  {
  public constructor() {
    super("dtcockpit.commands.StartMontiVerseLitePipeline");
  }

  public doRun (): Promise<StatusResult> {
  //  this._notificationService.notificationOkOnly('Projekt löschen', "OOO");
    throw new Error("Can only be executed in the backend!")
  }
}



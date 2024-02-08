/* (c) https://github.com/MontiCore/monticore */

export interface GeneralErrorObserver {
  notifyConnectionError(msg: string);

  notifyServerError(msg: string);

  notifySyncError(msg: string);
}



/* (c) https://github.com/MontiCore/monticore */

// RTE-P

export interface GeneralErrorObserver {
  notifyConnectionError(msg: String): void;
  notifyServerError(msg: String): void;
  // TODO: pass collection of gemIds with sync errors?
  notifySyncError(msg: String): void;
}

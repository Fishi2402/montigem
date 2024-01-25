/* (c) https://github.com/MontiCore/monticore */

import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {DialogCallback} from './dialog.callback';
import {DialogComponent} from './dialog.component';
import {noop, Observable} from 'rxjs';

export enum CloseReason {
  Confirm = 'confirm',
  Option = 'option',
  Deny = 'deny',
  Abort = 'abort',
  DontShowAgain = 'dontShowAgain'
}

export enum NotificationActionName {
  Yes = 'Ja',
  No = 'Nein',
  Ok = 'Ok',
  Cancel = 'Abbrechen'
}

@Injectable()
export class NotificationService {
  constructor(public dialog: MatDialog) {}

  public async notificationYesNo(title: string, msg: string,
    callback: DialogCallback | undefined = undefined,
    width: number = 500, close?: Observable<string>): Promise<boolean> {

    const result = await this.openDialog({title, msg,
      confirmName: NotificationActionName.Yes,
      denyName: NotificationActionName.No
    }, width, close);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOkOnly(title: string, msg: string,
    callback: DialogCallback | undefined = undefined,
    width: number = 500): Promise<boolean> {

    const result = await this.openDialog({title, msg,
      confirmName: NotificationActionName.Ok
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOkMultilines(title: string, msgs: string[],
    callback: DialogCallback | undefined = undefined, width: number = 500): Promise<boolean> {

    const result = await this.openDialog({title, msgs,
      confirmName: NotificationActionName.Ok
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOkCancel(title: string, msg: string,
    callback: DialogCallback | undefined = undefined, ok: NotificationActionName = NotificationActionName.Ok, width: number = 500): Promise<boolean> {

    const result = await this.openDialog({title, msg,
      confirmName: ok,
      denyName: NotificationActionName.Cancel
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public async notificationOptions(title: string, msg: string,
    confirmName: string, optionName: string, denyName: string, callback: DialogCallback | undefined = undefined, width: number = 500): Promise<boolean> {
    const result = await this.openDialog({title, msg,
      confirmName: confirmName,
      optionName: optionName,
      denyName: denyName
    }, width);

    if (result === CloseReason.Confirm) {
      callback ? await callback.onConfirm() : noop;
    } else if (result === CloseReason.Option) {
      callback ? await callback.onOption() : noop;
    } else {
      callback ? await callback.onDeny() : noop;
    }

    return result !== CloseReason.Deny;
  }

  public notYetAvailable() {
    this.notificationOkOnly('Information', 'Diese Funktion ist noch nicht verfügbar.', undefined);
  }

  public async error(error: string): Promise<boolean> {
    return this.notificationOkOnly(error,
      'Ein Systemfehler ist aufgetreten, bitte versuchen Sie es erneut.', undefined);
  }

  private openDialog<T>(data: any, width: number = 500, close?: Observable<string>): Promise<string> {
    const ref = this.dialog.open(DialogComponent, {
      panelClass: 'custom-dialog-container' ,
      width: `${width}px`,
      backdropClass: 'notification-backdrop',
      data: data
    });

    if (close) {
      close./*take(1).*/subscribe((dialogResult: string) => {
        ref.close(dialogResult);
      });
    }
    return new Promise<string>((resolve) => {
      ref.afterClosed().subscribe((result) => resolve(result));
    });
  }

}

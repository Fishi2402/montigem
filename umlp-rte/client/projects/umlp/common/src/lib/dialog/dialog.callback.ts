/* (c) https://github.com/MontiCore/monticore */

export interface DialogCallback {
  onConfirm: () => void;
  onOption: () => void;
  onDeny: () => void;
  onAbort: () => void;
}

export class DialogCallbackEmpty implements DialogCallback {
  public onConfirm(): void {}
  public onOption(): void {}
  public onDeny(): void {}
  public onAbort(): void {}
}

export class DialogCallbackConfirm implements DialogCallback {
  constructor(private onConfirmCallback: () => void) {}

  public onConfirm(): void {
    return this.onConfirmCallback();
  }
  public onOption(): void {}
  public onDeny(): void {}
  public onAbort(): void {}
}

export class DialogCallbackConfirmDeny implements DialogCallback {
  constructor(private onConfirmCallback: () => void, private onDenyCallback: () => void) {}

  public onConfirm(): void {
    return this.onConfirmCallback();
  }
  public onOption(): void {}
  public onDeny(): void {
    return this.onDenyCallback();
  }
  public onAbort(): void {}
}

export class DialogCallbackConfirmDenyOption implements DialogCallback {
  constructor(private onConfirmCallback: () => void, private onOptionCallback: () => void, private onDenyCallback: () => void) {
  }

  public onConfirm(): void {
    return this.onConfirmCallback();
  }
  public onOption(): void {
    return this.onOptionCallback();
  }
  public onDeny(): void {
    return this.onDenyCallback();
  }
  public onAbort(): void {}
}

export class DialogCallbackConfirmDenyOptionAbort implements DialogCallback {
  constructor(private onConfirmCallback: () => void,
              private onOptionCallback: () => void,
              private onDenyCallback: () => void,
              private onAbortCallback: () => void) {
  }

  public onConfirm(): void {
    return this.onConfirmCallback();
  }
  public onOption(): void {
    return this.onOptionCallback();
  }
  public onDeny(): void {
    return this.onDenyCallback();
  }
  public onAbort(): void {
    return this.onAbortCallback();
  }
}

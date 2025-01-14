/* (c) https://github.com/MontiCore/monticore */
export enum NotificationScope {
  MINIMAL, LOCAL, GLOBAL, ALL
}

/** @ignore */
export class NotificationScope_$WRAPPER {
  /*private*/
  level;

  constructor(protected _$ordinal: number, protected _$name: string, level) {
    if (this.level === undefined) {
      this.level = 0;
    }
    this.level = level;
  }

  public contains(other): boolean {
    return NotificationScope["_$wrappers"][other].level <= this.level;
  }

  public name(): string {
    return this._$name;
  }

  public ordinal(): number {
    return this._$ordinal;
  }

  public compareTo(other: any): number {
    return this._$ordinal - (isNaN(other) ? other._$ordinal : other);
  }
}

NotificationScope["__class"] = "umlp.backendrte.common.NotificationScope";
NotificationScope["_$wrappers"] = {
  0: new NotificationScope_$WRAPPER(0, "MINIMAL", 0),
  1: new NotificationScope_$WRAPPER(1, "LOCAL", 3),
  2: new NotificationScope_$WRAPPER(2, "GLOBAL", 4),
  3: new NotificationScope_$WRAPPER(3, "ALL", 5)
};




/* (c) https://github.com/MontiCore/monticore */

// RTE-P

export class NotificationScope {
  public static get MINIMAL() {
    return new NotificationScope(0);
  } // Events mit minimalem Einfluss, z.B. um bei bidirektionalen Assoziationen Ping Pong zu verhindern
  public static get LOCAL() {
    return new NotificationScope(3);
  } // Events mit lokalem Einfluss, also dieser Server/Client, z.B. setAge ausgelöst von einem Command
  public static get GLOBAL() {
    return new NotificationScope(4);
  } // Events mit Einfluss auf alle Clients und den Server, wie z.B. setAge ausgelöst von einer GUI
  public static get ALL() {
    return new NotificationScope(5);
  }

  private level: number;

  constructor(level: number) {
    this.level = level;
    return this;
  }

  public contains(other: NotificationScope): boolean {
    return other.level <= this.level;
  }
}

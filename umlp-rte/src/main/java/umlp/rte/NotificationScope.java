/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

// RTE-P

public enum NotificationScope {
  MINIMAL(0), // Events mit minimalem Einfluss, z.B. um bei bidirektionalen Assoziationen Ping Pong zu verhindern
  LOCAL(3), // Events mit lokalem Einfluss, also dieser Server/Client, z.B. setAge ausgelöst von einem Command
  GLOBAL(4), // Events mit Einfluss auf alle Clients und den Server, wie z.B. setAge ausgelöst von einer GUI
  ALL(5);

  private final int level;

  NotificationScope(int level) {
    this.level = level;
  }

  public boolean contains(NotificationScope other){
    return other.level <= level;
  }
}

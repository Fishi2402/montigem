/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

// RTE-P

public enum NotificationScope {
  MINIMAL(0), // Events with minimal impact, for example to prevent ping pong in bidirectional associations.
  LOCAL(3), // Events with local impact, meaning this server/client, for example setAge triggered by a command.
  GLOBAL(4), // Events with impact on all clients and the server, such as setAge triggered by a GUI.
  ALL(5);

  private final int level;

  NotificationScope(int level) {
    this.level = level;
  }

  public boolean contains(NotificationScope other){
    return other.level <= level;
  }
}

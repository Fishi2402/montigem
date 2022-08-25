package umlp.rte;

// RTE-P

public enum NotificationScope {
  ALL,
  LOCAL, // Events mit lokalem Einfluss, also dieser Server/Client, z.B. setAge ausgelöst von einem Command
  GLOBAL; // Events mit Einfluss auf alle Clients und den Server, wie z.B. setAge ausgelöst von einer GUI

  public boolean contains(NotificationScope other){
    if(ALL == this){
      return true;
    }

    return this == other;
  }
}

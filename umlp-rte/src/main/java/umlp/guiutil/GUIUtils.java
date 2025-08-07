/* (c) https://github.com/MontiCore/monticore */
package umlp.guiutil;

import umlp.backendrte.common.GemIdentifiable;

public class GUIUtils {
  public static void update(Runnable runnable) {
    runnable.run();
  }
  public static <T extends GemIdentifiable> T registerObserverToDomainObject(T domainObject) {
    return domainObject;
  }
  public static <T> T registerObserverToStateVar(T stateVar) {
    return stateVar;
  }
}

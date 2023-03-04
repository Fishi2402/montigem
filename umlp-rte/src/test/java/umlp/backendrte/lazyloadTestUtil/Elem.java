// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.lazyloadTestUtil;

import umlp.backendrte.common.GemIdentifiable;

/**
 * objects are used as testEntries
 * in a LazyList
 */
public class Elem implements GemIdentifiable {

  public static long nextID = 0;

  protected long gemID;

  public Elem() {
    gemID = nextID++;
  }

  @Override
  public long getGemId() {
    return gemID;
  }

  @Override
  public String toString() {
    return "Elem{" +
        "gemID=" + gemID +
        '}';
  }
}

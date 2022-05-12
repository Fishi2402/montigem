// (c) https://github.com/MontiCore/monticore
package umlp.rte.lazyloadTestUtil;

import umlp.rte.GemIdentifiable;

/**
 * objects are used as testEntries
 * in a LazyList
 */
public class Elem implements GemIdentifiable {

  protected static long nextID = 0;

  protected long gemID;

  public Elem() {
    gemID = nextID++;
  }

  @Override
  public long getGemId() {
    return gemID;
  }

}

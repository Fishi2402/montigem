// (c) https://github.com/MontiCore/monticore
package umlptest.listTestUtil;

import umlp.util.GemIdentifiable;

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

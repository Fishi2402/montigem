/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

// RTE-P

import java.util.List;

/**
 * Daten die zur Generierung einer GemList eines bestimmen Typen benötigt werden
 */
public class GemListDeserHelper {
  private long gemID;

  private int size;

  private List<Long> initialIds;

  public GemListDeserHelper(long gemId, int size, List<Long> ids) {
    this.gemID = gemId;
    this.size = size;
    this.initialIds = ids;
  }

  public long getGemID() {
    return gemID;
  }

  public int getSize() {
    return size;
  }

  public List<Long> getInitialIds() {
    return initialIds;
  }
}

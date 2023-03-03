/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

public class AssociationDBEntry {
  final private long lhsGemId;
  final private long rhsGemId;
  final private int positionInList;

  public AssociationDBEntry(long lhsGemId, long rhsGemId, int positionInList) {
    this.lhsGemId = lhsGemId;
    this.rhsGemId = rhsGemId;
    this.positionInList = positionInList;
  }


  public long getLhsGemId() {
    return lhsGemId;
  }

  public long getRhsGemId() {
    return rhsGemId;
  }

  public int getPositionInList() {
    return positionInList;
  }
}

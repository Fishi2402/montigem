/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

// RTE-P

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Verwaltet alle gemIds einer GemList.
// Ziel ist es, gemIds in einer dünn besetzten Datenstruktur zu verwalten um das Arbeiten mit
// sehr großen Assoziationen zu ermöglichen.
// Dafür müssen die Indicies von IDs angepasst werden, wenn weiter vorne in der GemList IDs hinzugefügt
// oder entfernt werden.
public interface IdStorage {
  Optional<Long> getId(int idx);

  void addAll(int i, List<Long> ids);

  void set(Map<Integer, Long> changes);

  void remove(Collection<Integer> indicies);

  boolean containsIdx(int idx);

  void clear();

  int size();

  boolean removeId(long gemId);
}

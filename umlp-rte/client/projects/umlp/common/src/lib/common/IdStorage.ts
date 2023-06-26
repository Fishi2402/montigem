/* (c) https://github.com/MontiCore/monticore */

// RTE-P

// Verwaltet alle gemIds einer GemList.
// Ziel ist es, gemIds in einer dünn besetzten Datenstruktur zu verwalten um das Arbeiten mit
// sehr großen Assoziationen zu ermöglichen.
// Dafür müssen die Indicies von IDs angepasst werden, wenn weiter vorne in der GemList IDs hinzugefügt
// oder entfernt werden.
export interface IdStorage {
  getId(idx: number): number ;

  addAll(i: number, ids: Array<number>): void ;

  set(changes: Map<number, number> ): void ;

  remove(indicies: Array<number>): void ;

  containsIdx(idx: number): boolean ;

  clear(): void ;

  size(): number ;

  removeId(gemId: number): boolean ;
}

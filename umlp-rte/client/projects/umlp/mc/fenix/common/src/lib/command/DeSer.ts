/* (c) https://github.com/MontiCore/monticore */

export interface DeSer<T> {
  serialize(obj: T): string;
  deserialize(str: string): T;
}

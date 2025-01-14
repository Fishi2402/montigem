/* (c) https://github.com/MontiCore/monticore */
import {IUMLPList} from '../common/IUMLPList';

export interface IdStorage {
  getId(idx: number): number;

  addAll(i: number, ids: IUMLPList<number>);

  set(changes: any);

  remove(indicies: Array<number> | IUMLPList<number>);

  containsIdx(idx: number): boolean;

  clear();

  size(): number;

  removeId(gemId: number): boolean;
}



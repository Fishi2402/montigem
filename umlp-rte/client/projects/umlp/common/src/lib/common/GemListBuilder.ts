/* (c) https://github.com/MontiCore/monticore */

import { GemBuildable } from "./GemBuildable";
import { GemIdentifiable } from "./GemIdentifiable";
import { GemList } from "./GemList";
import { IDOrBuilder } from "./IDOrBuilder";

// RTE-P

// GemList, welche bei der Erstellung von Objekten benutzt werden muss, da sie in diesem Zustand
// noch nicht mit anderen Teilnehmen synchronisiert werden kann.
export abstract class GemListBuilder<E extends GemIdentifiable & GemBuildable> extends GemList<E> {
  protected buildFinished: boolean = false;

  // Da einem in einem Builder andere Builder hinzugefügt werden können und der FlensburgManager diese
  // nicht kennt, also auch nicht per id laden kann, muss hier eine lokale Verwaltung stattfinden.
  // Nach build() wird diese geleert und nicht weiter verwendet.
  protected builderElemStorage: Array<IDOrBuilder<E>> = [];

  public constructor(initialIds: Array<number>) {
    super();
    super.GemList2(initialIds);
    for (const gemID of initialIds) {
      this.builderElemStorage.push(new IDOrBuilder<E>().IDOrBuilder1(gemID));
    }
  }

  // TODO AGe: unclean
  public override build(): any {
    if (this.buildFinished) {
      return <any>this;
    }

    this.ids = [];
    for (const element of this.builderElemStorage) {
      this.ids.push(element.getIDAndMaybeBuild());
    }

    this.buildFinished = true;

    // Von hier an werden alle Element vom FlensburgManager geladen
    this.builderElemStorage = [];

    return <any>this;
  }

  public override isBuildFinished(): boolean {
    return this.buildFinished;
  }

  public override size(): number {
    if (this.buildFinished) {
      return super.size();
    } else {
      return this.builderElemStorage.length;
    }
  }

  public override get(idx: number): E {
    if (this.buildFinished) {
      return super.get(idx);
    } else {
      // List might contain builders, which a normal GemList will not
      // Thus, a local data storage is needed
      if (this.builderElemStorage[idx].isPresentBuilder()) {
        return this.builderElemStorage[idx].getBuilder();
      } else {
        return super.get(idx);
      }
    }
  }

  public override add(idx: number, elem: E): void {
    if (this.buildFinished) {
      super.add(idx, elem);
    } else {
      this.builderElemStorage.splice(idx, 0, new IDOrBuilder<E>().IDOrBuilder2(elem));
    }
  }

  /*
  public override _numberernal_set(idx: number, gemID: number): void  {
    if(this.buildFinished) {
      super._numberernal_set(idx, gemID);
    } else {
      this.builderElemStorage[idx] = new IDOrBuilder<E>().IDOrBuilder1(gemID);
    }
  }

  @Override
  public void _numberernal_add(number idx, List<number> additions) {
    if(buildFinished) {
      super._numberernal_add(idx, additions);
    } else {
      List<IDOrBuilder<E>> newElems = new ArrayList<>();
      for (number e : additions) {
        IDOrBuilder<E> eidOrBuilder = new IDOrBuilder<>(e);
        newElems.add(eidOrBuilder);
      }
      builderElemStorage.addAll(idx, newElems);
    }
  }

  @Override
  public void _numberernal_remove(Set<numbereger> idxs) {
    if(buildFinished) {
      super._numberernal_remove(idxs);
    } else {
      List<numbereger> idxsReversed = new ArrayList<>();
      for (numbereger numbereger : idxs) {
        idxsReversed.add(numbereger);
      }
      Collections.reverse(idxsReversed);
      for(number idx : idxsReversed) {
        builderElemStorage.remove(idx);
      }
    }
  }
  /**/

  public override getIDs(startIdx?: number, endIdx?: number): Array<number> {
    if (startIdx != null && endIdx != null) {
      if (this.buildFinished) {
        return super.getIDs(startIdx, endIdx);
      } else {
        let ids: Array<number> = [];
        for (let idx = startIdx; idx < endIdx; idx++) {
          if (this.builderElemStorage[idx].isPresentID()) {
            ids.push(this.builderElemStorage[idx].getID());
          } else {
            //todo decide on error handling
            ids.push(-1);
          }
        }
        return ids;
      }
    }
    if (this.buildFinished) {
      return super.getIDs();
    } else {
      return this.getIDs(0, this.builderElemStorage.length);
    }
  }

}

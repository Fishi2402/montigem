// (c) https://github.com/MontiCore/monticore

import { GemBuildable } from "./GemBuildable";
import { GemIdentifiable } from "./GemIdentifiable";
import { LoadingState } from "./LoadingState";
import { LoadResult } from "./LoadResult";

// TODO: Brauchen wir hier Observer, oder werden die Observer eine ebene höher genutzt?
// Sonst kann es zu Dopplung kommen, z.B. gibt es bei Person#addOwns die Methode PersonObserver#notifyAddOwns,
// welche Überschnitt mit GemListObserver#notifyAddIDs hätte.

// TODO JSweet: Sollte vielleicht besser den Arrayprototypen überschreiben
// Array.prototype.remove = function (o) {
//    // code to remove "o"
//    return this;
//}

/*
 * A lazy list
 * Elements are loaded as they are required.
 * Useful if e.g. the elements are loaded over a network.
 */
export class GemListIterator<T> implements Iterator<T> {
  private index: number;
  private done: boolean;
  constructor(private values: T[]) {
    this.index = 0;
    this.done = false;
  }

  next(): IteratorResult<T> {
    if (this.done) {
      return {
        done: this.done,
        value: undefined
      }
    }
    if (this.index === this.values.length) {
      this.done = true;
      return {
        done: this.done,
        value: this.index
      }
    }
    const value = this.values[this.index];
    ++this.index;
    return {
      done: false,
      value: value
    }
  }
}

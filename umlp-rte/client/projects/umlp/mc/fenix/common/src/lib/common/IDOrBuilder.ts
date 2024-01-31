// (c) https://github.com/MontiCore/monticore

import { GemBuildable } from "./GemBuildable";
import { GemIdentifiable } from "./GemIdentifiable";

// Solely used in GemListBuilder
// SumType of a domain-objects GemID and builder
// as the builder may not have an ID that can be used to get the element from the manager
export class IDOrBuilder<E extends GemIdentifiable & GemBuildable> {

  gemIDOpt: number = null;

  builderOpt: E = null;

  public IDOrBuilder1(gemID: number): IDOrBuilder<E> {
    this.gemIDOpt = gemID;
    return this;
  }

  public IDOrBuilder2(builder: E): IDOrBuilder<E> {
    if (builder.isBuildFinished()) {
      this.gemIDOpt = builder.getGemId();
    }
    else {
      this.builderOpt = builder;
    }
    return this;
  }

  public isPresentID(): boolean {
    return this.gemIDOpt != null;
  }

  public isPresentBuilder(): boolean {
    return this.builderOpt != null;
  }

  public getID(): number {
    return this.gemIDOpt;
  }

  public getBuilder(): E {
    return this.builderOpt;
  }

  /**
   * builds the element if necessary and returns its id
   * @return the id of the element, it can be used to retrieve the object from the manager
   */
  public getIDAndMaybeBuild(): number {
    if (this.isPresentID()) {
      return this.gemIDOpt;
    }
    else {
      let builder: E = this.builderOpt;
      if (!builder.isBuildFinished()) {
        builder.build();
      }
      return builder.getGemId();
    }
  }

}

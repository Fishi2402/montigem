/* (c) https://github.com/MontiCore/monticore */

// Solely used in GemListBuilder
// SumType of a domain-objects GemID and builder
// as the builder may not have an ID that can be used to get the element from the manager
export class IDOrBuilder<E extends GemIdentifiable & GemBuildable> {
  gemIDOpt: number;

  builderOpt: E = null;

  public constructor(gemIdOrBuilder: number | E) {
    this.gemIDOpt = null;
    this.builderOpt = null;
    if (typeof gemIdOrBuilder === 'number') {
      this.gemIDOpt = (v => {
        if (v == null) throw new Error('value is null');
        return v;
      })(gemIdOrBuilder);
    } else {
      if (gemIdOrBuilder.isBuildFinished()) {
        this.gemIDOpt = (v => {
          if (v == null) throw new Error('value is null');
          return v.getGemId();
        })(gemIdOrBuilder);
      } else {
        this.builderOpt = (v => {
          if (v == null) throw new Error('value is null');
          return v;
        })(gemIdOrBuilder);
      }
    }
  }

  public isPresentID(): boolean {
    return /* isPresent */(this.gemIDOpt != null);
  }

  public isPresentBuilder(): boolean {
    return /* isPresent */(this.builderOpt != null);
  }

  public getID(): number {
    return /* get */(v => {
      if (v == null) throw new Error('value is null');
      return v;
    })(this.gemIDOpt);
  }

  public getBuilder(): E {
    return /* get */(v => {
      if (v == null) throw new Error('value is null');
      return v;
    })(this.builderOpt);
  }

  /**
   * builds the element if necessary and returns its id
   * @return {number} the id of the element, it can be used to retrieve the object from the manager
   */
  public async getIDAndMaybeBuild(): Promise<number> {
    if (this.isPresentID()) {
      return /* get */(v => {
        if (v == null) throw new Error('value is null');
        return v;
      })(this.gemIDOpt);
    } else {
      const builder: E = /* get */(v => {
        if (v == null) throw new Error('value is null');
        return v;
      })(this.builderOpt);
      if (!builder.isBuildFinished()) {
        await builder.build();
      }
      return builder.getGemId();
    }
  }
}

IDOrBuilder["__class"] = "umlp.backendrte.common.IDOrBuilder";


import {GemBuildable} from '../common/GemBuildable';


import {GemIdentifiable} from './GemIdentifiable';

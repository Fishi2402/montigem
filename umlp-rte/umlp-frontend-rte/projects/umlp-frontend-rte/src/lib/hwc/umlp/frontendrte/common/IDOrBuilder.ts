/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
export class IDOrBuilder<E extends GemIdentifiable & GemBuildable> {
    gemIDOpt: number;

    builderOpt: E;

    public constructor(builder?: any) {
        if (((builder != null) || builder === null)) {
            let __args = arguments;
            this.gemIDOpt = null;
            this.builderOpt = null;
            if (builder.isBuildFinished()){
                this.gemIDOpt = (v => { if (v == null) throw new Error('value is null'); return v; })(builder.getGemId());
            } else {
                this.builderOpt = (v => { if (v == null) throw new Error('value is null'); return v; })(builder);
            }
        } else if (((typeof builder === 'number') || builder === null)) {
            let __args = arguments;
            let gemID: any = __args[0];
            this.gemIDOpt = null;
            this.builderOpt = null;
            this.gemIDOpt = (v => { if (v == null) throw new Error('value is null'); return v; })(gemID);
        } else throw new Error('invalid overload');
    }

    public isPresentID(): boolean {
        return /* isPresent */(this.gemIDOpt != null);
    }

    public isPresentBuilder(): boolean {
        return /* isPresent */(this.builderOpt != null);
    }

    public getID(): number {
        return /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(this.gemIDOpt);
    }

    public getBuilder(): E {
        return /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(this.builderOpt);
    }

    /**
     * builds the element if necessary and returns its id
     * @return {number} the id of the element, it can be used to retrieve the object from the manager
     */
    public async getIDAndMaybeBuild(): Promise<number> {
        if (this.isPresentID()){
            return /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(this.gemIDOpt);
        } else {
            const builder: E = /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(this.builderOpt);
            if (!builder.isBuildFinished()){
                await builder.build();
            }
            return builder.getGemId();
        }
    }
}
IDOrBuilder["__class"] = "umlp.backendrte.common.IDOrBuilder";




import { GemBuildable } from '../common/GemBuildable';


import { GemIdentifiable } from './GemIdentifiable';


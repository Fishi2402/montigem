/* (c) https://github.com/MontiCore/monticore */

import { DeSerRepository } from '@umlp/commonj2ts';
import { GemTextInputValidityDeSer } from '.';

export class GemTextInputTypesDeSer implements DeSerRepository {
    deSers: any;

    static _gemTextInputTypesDeSer: GemTextInputTypesDeSer = null;

    public getDeSerMap(): any {
        /* put */(this.deSers["mc.fenix.input.gemtextinputtypes.GemTextInputValidity"] = new GemTextInputValidityDeSer());
        return this.deSers;
    }

    public static getInstance(): GemTextInputTypesDeSer {
        if (GemTextInputTypesDeSer._gemTextInputTypesDeSer == null){
            GemTextInputTypesDeSer._gemTextInputTypesDeSer = new GemTextInputTypesDeSer();
        }
        return GemTextInputTypesDeSer._gemTextInputTypesDeSer;
    }

    constructor() {
        this.deSers = <any>({});
    }
}
GemTextInputTypesDeSer["__class"] = "mc.fenix.input.gemtextinputtypes.GemTextInputTypesDeSer";
GemTextInputTypesDeSer["__interfaces"] = ["mc.fenix.commonj2ts.deser.DeSerRepository"];





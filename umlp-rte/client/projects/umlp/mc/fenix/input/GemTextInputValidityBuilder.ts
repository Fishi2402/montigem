/* (c) https://github.com/MontiCore/monticore */

import { GemTextInputValidity } from '.';

export class GemTextInputValidityBuilder extends GemTextInputValidity {
    buildFinished: boolean;

    realBuilder: GemTextInputValidityBuilder;

    public constructor() {
        super();
        this.buildFinished = false;
        if (this.realBuilder === undefined) { this.realBuilder = null; }
        this.realBuilder = <GemTextInputValidityBuilder><unknown>this;
    }

    public isBuildFinished(): boolean {
        return this.buildFinished;
    }

    public setBuildFinished(buildFinished: boolean) {
        this.buildFinished = buildFinished;
    }

    public status(status: string): GemTextInputValidityBuilder {
        this._status = status;
        return this.realBuilder;
    }

    checkStatusPresent(): boolean {
        return true;
    }

    public message(message: string): GemTextInputValidityBuilder {
        this._message = message;
        return this.realBuilder;
    }

    checkMessagePresent(): boolean {
        return true;
    }

    /**
     * 
     * @return {Optional}
     */
    public build(): GemTextInputValidity {
        if (!this.buildFinished) {
            if (!this.isBuildable()) {
                return null;
            }
            this.buildFinished = true;
        }
        return (v => { if (v == null) throw new Error('value is null'); return v; })(this);
    }

    public isBuildable(): boolean {
        return ((lhs, rhs) => lhs && rhs)((this.checkStatusPresent() && this.checkStatus()), (this.checkMessagePresent() && this.checkMessage()));
    }
}
GemTextInputValidityBuilder["__class"] = "mc.fenix.input.gemtextinputtypes.GemTextInputValidityBuilder";

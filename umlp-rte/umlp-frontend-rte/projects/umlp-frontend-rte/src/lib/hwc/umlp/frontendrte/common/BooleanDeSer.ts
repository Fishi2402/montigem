/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { DeSer } from '../command/DeSer';
import { StringHelper } from '../javautil/StringHelper';

export class BooleanDeSer implements DeSer<boolean> {
    public async serialize$java_lang_Boolean(obj: boolean): Promise<string> {
        return "" + obj;
    }

    /**
     *
     * @param {boolean} obj
     * @return {string}
     */
    public async serialize(obj?: any): Promise<string> {
        if (((typeof obj === 'boolean') || obj === null)) {
            return <any>await this.serialize$java_lang_Boolean(obj);
        } else throw new Error('invalid overload');
    }

    /**
     *
     * @param {string} str
     * @return {Optional}
     */
    public async deserialize(str: string): Promise<boolean> {
        const e: any = JSON.parse(str);
        if (!e.isJsonBoolean()){
            return null;
        }
        return (v => { if (v == null) throw new Error('value is null'); return v; })(e.getAsJsonBoolean().getValue());
    }

    constructor() {
    }
}
BooleanDeSer["__class"] = "umlp.backendrte.common.BooleanDeSer";
BooleanDeSer["__interfaces"] = ["umlp.backendrte.command.DeSer"];





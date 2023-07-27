/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { DeSer } from '../command/DeSer';
import { StringHelper } from '../javautil/StringHelper';

export class LongDeSer implements DeSer<number> {
    public async serialize$java_lang_Long(obj: number): Promise<string> {
        return "" + obj;
    }

    /**
     *
     * @param {number} obj
     * @return {string}
     */
    public async serialize(obj?: any): Promise<string> {
        if (((typeof obj === 'number') || obj === null)) {
            return <any>await this.serialize$java_lang_Long(obj);
        } else throw new Error('invalid overload');
    }

    /**
     *
     * @param {string} str
     * @return {Optional}
     */
    public async deserialize(str: string): Promise<number> {
        const e: any = JSON.parse(str);
        if (!e.isJsonNumber()){
            return null;
        }
        return (v => { if (v == null) throw new Error('value is null'); return v; })(e.getAsJsonNumber().getNumberAsLong());
    }

    constructor() {
    }
}
LongDeSer["__class"] = "umlp.backendrte.common.LongDeSer";
LongDeSer["__interfaces"] = ["umlp.backendrte.command.DeSer"];





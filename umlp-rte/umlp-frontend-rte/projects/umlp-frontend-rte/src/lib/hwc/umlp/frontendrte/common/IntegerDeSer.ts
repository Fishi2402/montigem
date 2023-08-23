/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { DeSer } from '../command/DeSer';
import { StringHelper } from '../javautil/StringHelper';

export class IntegerDeSer implements DeSer<number> {
    public async serialize$java_lang_Integer(obj: number): Promise<string> {
        return "" + obj;
    }

    /**
     *
     * @param {number} obj
     * @return {string}
     */
    public async serialize(obj?: any): Promise<string> {
        if (((typeof obj === 'number') || obj === null)) {
            return <any>await this.serialize$java_lang_Integer(obj);
        } else throw new Error('invalid overload');
    }

    /**
     *
     * @param {string} str
     * @return {Optional}
     */
    public async deserialize(str: string): Promise<number> {
        const e: any = JSON.parse(str);
        if (typeof e != 'number'){
            return null;
        }
        return (v => { if (v == null) throw new Error('value is null'); return v; })(e);
    }

    constructor() {
    }
}
IntegerDeSer["__class"] = "umlp.backendrte.common.IntegerDeSer";
IntegerDeSer["__interfaces"] = ["umlp.backendrte.command.DeSer"];





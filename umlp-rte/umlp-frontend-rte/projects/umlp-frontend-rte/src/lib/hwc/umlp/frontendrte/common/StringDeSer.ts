/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { DeSer } from '../command/DeSer';
import { StringHelper } from '../javautil/StringHelper';

export class StringDeSer implements DeSer<string> {
    public async serialize$java_lang_String(obj: string): Promise<string> {
        return "\"" + obj + "\"";
    }

    /**
     *
     * @param {string} obj
     * @return {string}
     */
    public async serialize(obj?: any): Promise<string> {
        if (((typeof obj === 'string') || obj === null)) {
            return <any>await this.serialize$java_lang_String(obj);
        } else throw new Error('invalid overload');
    }

    /**
     *
     * @param {string} str
     * @return {Optional}
     */
    public async deserialize(str: string): Promise<string> {
        const e: any = JSON.parse(str);
        if (typeof e != 'string'){
            return null;
        }
        return (v => { if (v == null) throw new Error('value is null'); return v; })(e);
    }

    constructor() {
    }
}
StringDeSer["__class"] = "umlp.backendrte.common.StringDeSer";
StringDeSer["__interfaces"] = ["umlp.backendrte.command.DeSer"];





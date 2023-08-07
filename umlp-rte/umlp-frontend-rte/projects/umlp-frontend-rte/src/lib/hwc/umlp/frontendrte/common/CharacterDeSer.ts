/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { DeSer } from '../command/DeSer';
import { StringHelper } from '../javautil/StringHelper';

export class CharacterDeSer implements DeSer<string> {
    public async serialize$java_lang_Character(obj: string): Promise<string> {
        return "" + obj;
    }

    /**
     *
     * @param {string} obj
     * @return {string}
     */
    public async serialize(obj?: any): Promise<string> {
        if (((typeof obj === 'string') || obj === null)) {
            return <any>await this.serialize$java_lang_Character(obj);
        } else throw new Error('invalid overload');
    }

    /**
     *
     * @param {string} str
     * @return {Optional}
     */
    public async deserialize(str: string): Promise<string> {
        const e: any = JSON.parse(str);
        if (typeof e != 'number'){
            return null;
        }
        return (v => { if (v == null) throw new Error('value is null'); return v; })(String.fromCharCode(e));
    }

    constructor() {
    }
}
CharacterDeSer["__class"] = "umlp.backendrte.common.CharacterDeSer";
CharacterDeSer["__interfaces"] = ["umlp.backendrte.command.DeSer"];





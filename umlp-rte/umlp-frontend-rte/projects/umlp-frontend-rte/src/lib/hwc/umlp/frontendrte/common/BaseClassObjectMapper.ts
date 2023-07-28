/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Log } from '../logging/Log';
import {BooleanDeSer} from "../common/BooleanDeSer";
import {ShortDeSer} from "../common/ShortDeSer";
import {CharacterDeSer} from "../common/CharacterDeSer";
import {LongDeSer} from "../common/LongDeSer";
import {IntegerDeSer} from "../common/IntegerDeSer";
import {ByteDeSer} from "../common/ByteDeSer";
import {FloatDeSer} from "../common/FloatDeSer";
import {DoubleDeSer} from "../common/DoubleDeSer";
import {StringDeSer} from "../common/StringDeSer";
import {LocalDateDeSer} from "../deser";

/**
 * (De)serializer for base classes from java.lang
 * Since this uses the static delegate pattern, additional base classes can be added via subclassing.
 * (See FlensburgBaseClassObjectMapper)
 * @class
 */
export class BaseClassObjectMapper {
    static instance: BaseClassObjectMapper; public static instance_$LI$(): BaseClassObjectMapper { if (BaseClassObjectMapper.instance == null) { BaseClassObjectMapper.instance = new BaseClassObjectMapper(); }  return BaseClassObjectMapper.instance; }

    public static getInstance(): BaseClassObjectMapper {
        return BaseClassObjectMapper.instance_$LI$();
    }

    baseDeSers: any;

    public constructor() {
        this.baseDeSers = <any>({});
        /* put */(this.baseDeSers["java.lang.Boolean"] = new BooleanDeSer());
        /* put */(this.baseDeSers["java.lang.Short"] = new ShortDeSer());
        /* put */(this.baseDeSers["java.lang.Character"] = new CharacterDeSer());
        /* put */(this.baseDeSers["java.lang.Long"] = new LongDeSer());
        /* put */(this.baseDeSers["java.lang.Integer"] = new IntegerDeSer());
        /* put */(this.baseDeSers["java.lang.Byte"] = new ByteDeSer());
        /* put */(this.baseDeSers["java.lang.Float"] = new FloatDeSer());
        /* put */(this.baseDeSers["java.lang.Double"] = new DoubleDeSer());
        /* put */(this.baseDeSers["java.lang.String"] = new StringDeSer());
        /* put */(this.baseDeSers["java.time.LocalDate"] = new LocalDateDeSer());
    }

    public static serialize(o: any, className: string): string {
        return BaseClassObjectMapper.getInstance()._serialize(o, className);
    }

    public _serialize(o: any, className: string): string {
        if (!/* containsKey */this.baseDeSers.hasOwnProperty(className)){
            return null;
        }
        return /* get */((m,k) => m[k]===undefined?null:m[k])(this.baseDeSers, className).serialize(o);
    }

    public static deserialize<T>(s: string, className: string): T {
        return BaseClassObjectMapper.getInstance()._deserialize<any>(s, className);
    }

    public _deserialize<T>(s: string, className: string): T {
        if (!/* containsKey */this.baseDeSers.hasOwnProperty(className)){
            return null;
        }
        try {
            return <T>/* get */((m,k) => m[k]===undefined?null:m[k])(this.baseDeSers, className).deserialize(s);
        } catch(ex) {
            Log.error("Wrong return type of base class deser: \'" + className + "\'");
            return null;
        }
    }

    public static canDeSer(className: string): boolean {
        return BaseClassObjectMapper.getInstance()._canDeSer(className);
    }

    public _canDeSer(className: string): boolean {
        return /* containsKey */this.baseDeSers.hasOwnProperty(className);
    }
}
BaseClassObjectMapper["__class"] = "umlp.backendrte.common.BaseClassObjectMapper";


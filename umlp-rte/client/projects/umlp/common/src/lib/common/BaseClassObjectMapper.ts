/* (c) https://github.com/MontiCore/monticore */

import { DeSer } from "../command/DeSer";
import { BooleanDeSer } from "./BooleanDeSer";
import { ByteDeSer } from "./ByteDeSer";
import { CharacterDeSer } from "./CharacterDeSer";
import { DoubleDeSer } from "./DoubleDeSer";
import { FloatDeSer } from "./FloatDeSer";
import { IntegerDeSer } from "./IntegerDeSer";
import { LocalDateDeSer } from "./LocalDateDeSer";
import { LongDeSer } from "./LongDeSer";
import { ShortDeSer } from "./ShortDeSer";
import { StringDeSer } from "./StringDeSer";

// RTE-P

/**
 * (De)serializer for base classes from java.lang
 * Since this uses the static delegate pattern, additional base classes can be added via subclassing.
 * (See FlensburgBaseClassObjectMapper)
 */
export class BaseClassObjectMapper {
  protected static instance: BaseClassObjectMapper = new BaseClassObjectMapper();

  protected static setInstance(instance: BaseClassObjectMapper): void {
    BaseClassObjectMapper.instance = instance;
  }

  public static getInstance(): BaseClassObjectMapper {
    return this.instance;
  }

  protected baseDeSers: Map<string, DeSer<any>> = new Map();

  public constructor() {
    this.baseDeSers.set("java.lang.Boolean", new BooleanDeSer());
    this.baseDeSers.set("java.lang.Short", new ShortDeSer());
    this.baseDeSers.set("java.lang.Character", new CharacterDeSer());
    this.baseDeSers.set("java.lang.Long", new LongDeSer());
    this.baseDeSers.set("java.lang.Integer", new IntegerDeSer());
    this.baseDeSers.set("java.lang.Byte", new ByteDeSer());
    this.baseDeSers.set("java.lang.Float", new FloatDeSer());
    this.baseDeSers.set("java.lang.Double", new DoubleDeSer());
    this.baseDeSers.set("java.lang.String", new StringDeSer());

    this.baseDeSers.set("java.time.LocalDate", new LocalDateDeSer());
  }

  public static serialize(o: any, className: string): string {
    // return JSON.stringify(o);
    return this.getInstance()._serialize(o, className);
  }

  public _serialize(o: any, className: string): string {
    if (!this.baseDeSers.has(className)) {
      return null;
    }

    return this.baseDeSers.get(className).serialize(o);
  }

  public static deserialize<T>(s: string, className: string): T {
    return this.getInstance()._deserialize(s, className);
  }

  public _deserialize<T>(s: string, className: string): T {
    if (!this.baseDeSers.has(className)) {
      return null;
    }
    // TODO prüfen
    return <T>this.baseDeSers.get(className).deserialize(s);
  }

  public static canDeSer(className: string): boolean {
    return this.getInstance()._canDeSer(className);
  }

  public _canDeSer(className: string): boolean {
    return this.baseDeSers.has(className);
  }
}

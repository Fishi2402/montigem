/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { StringHelper } from '../javautil/StringHelper';

export interface DeSer<T> {
    serialize(obj: T): Promise<string>;

    deserialize(str: string): Promise<T>;
}



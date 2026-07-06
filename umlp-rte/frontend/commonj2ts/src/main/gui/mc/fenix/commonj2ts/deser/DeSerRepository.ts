/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { JsonDeSer } from '../command/JsonDeSer';

export interface DeSerRepository {
    getDeSerMap(): Promise<Map<string, JsonDeSer<any>>>;
}

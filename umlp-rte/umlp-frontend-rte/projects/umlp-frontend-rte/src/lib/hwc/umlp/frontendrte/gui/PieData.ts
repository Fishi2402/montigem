/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { StringHelper } from '../javautil/StringHelper';

export class PieData {
    description: string;

    value: number;

    public constructor(description: string, value: number) {
        if (this.description === undefined) { this.description = null; }
        if (this.value === undefined) { this.value = 0; }
        this.description = description;
        this.value = value;
    }

    public getValue(): number {
        return this.value;
    }

    public getDescription(): string {
        return this.description;
    }

    public setValue(value: number) {
        this.value = value;
    }

    public setDescription(description: string) {
        this.description = description;
    }
}
PieData["__class"] = "umlp.backendrte.gui.PieData";




/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { StringHelper } from '../javautil/StringHelper';

export class ValidationMessage {
    /*private*/ message: string;

    constructor(message: string) {
        if (this.message === undefined) { this.message = null; }
        this.message = message;
    }

    public getMessage(): string {
        return this.message;
    }

    public setMessage(message: string) {
        this.message = message;
    }

    public static create(message: string): ValidationMessage {
        return new ValidationMessage(message);
    }
}
ValidationMessage["__class"] = "umlp.backendrte.common.ValidationMessage";




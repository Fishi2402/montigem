/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Command } from '../command/Command';
import { StringHelper } from '../javautil/StringHelper';

export abstract class Request<T> extends Command<T> {
    constructor(commandType: string) {
        super(commandType);
    }

    /**
     *
     * @return {boolean}
     */
    public isRequest(): boolean {
        return true;
    }
}
Request["__class"] = "umlp.backendrte.request.Request";




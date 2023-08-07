/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { StringHelper } from '../javautil/StringHelper';

export interface GeneralErrorObserver {
    notifyConnectionError(msg: string);

    notifyServerError(msg: string);

    notifySyncError(msg: string);
}



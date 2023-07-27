/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { CommandManager } from '../command/CommandManager';
// import { ServerError } from '../command/ServerError';
import { SyncError } from '../command/SyncError';
import { UMLPArrayList } from '../common/UMLPArrayList';
import { IUMLPList } from '../common/IUMLPList';
import { StringHelper } from '../javautil/StringHelper';

export class GeneralErrorCollector {
    static instance: GeneralErrorCollector = null;

    observers: IUMLPList<GeneralErrorObserver>;

    public static init() {
        GeneralErrorCollector.instance = new GeneralErrorCollector();
    }

    public static getInstance(): GeneralErrorCollector {
        if (GeneralErrorCollector.instance == null){
            GeneralErrorCollector.init();
        }
        return GeneralErrorCollector.instance;
    }

    public static addErrorObserver(observer: GeneralErrorObserver) {
        GeneralErrorCollector.getInstance()._addErrorObserver(observer);
    }

    public static initMe(generalErrorCollector: GeneralErrorCollector) {
        GeneralErrorCollector.instance = generalErrorCollector;
    }

    async _addErrorObserver(observer: GeneralErrorObserver) {
        await this.observers.add(observer);
    }

    public static async containsErrorObserver(observer: GeneralErrorObserver): Promise<boolean> {
        return await GeneralErrorCollector.getInstance()._containsErrorObserver(observer);
    }

    async _containsErrorObserver(observer: GeneralErrorObserver): Promise<boolean> {
        return await this.observers.contains(observer);
    }

    public static async removeErrorObserver(observer: GeneralErrorObserver): Promise<boolean> {
        return await GeneralErrorCollector.getInstance()._removeErrorObserver(observer);
    }

    async _removeErrorObserver(observer: GeneralErrorObserver): Promise<boolean> {
        return await this.observers.removeElement(observer);
    }

    public static reportConnectionError() {
        GeneralErrorCollector.getInstance()._reportConnectionError();
    }

    _reportConnectionError() {
        const msg: string = this._buildConnectionErrorMsg();
        this.observers.forEach(((msg) => {
            return (obs) => obs.notifyConnectionError(msg)
        })(msg));
    }

    _buildConnectionErrorMsg(): string {
        return "Connection error!";
    }

    public static reportServerError(t: Error) {
        GeneralErrorCollector.getInstance()._reportServerError(t);
    }

    _reportServerError(t: Error) {
        // CommandManager.sendCommandToSubscribers<any>(new ServerError(t));
        const msg: string = this._buildServerErrorMsg(t);
        this.observers.forEach(((msg) => {
            return (obs) => obs.notifyServerError(msg)
        })(msg));
    }

    _buildServerErrorMsg(t: Error): string {
        return "Server error: " + t.message;
    }

    public static reportSyncError() {
        GeneralErrorCollector.getInstance()._reportSyncError();
    }

    _reportSyncError() {
        // CommandManager.sendCommandToSubscribers<any>(new SyncError());
        const msg: string = this._buildSyncErrorMsg();
        this.observers.forEach(((msg) => {
            return (obs) => obs.notifySyncError(msg)
        })(msg));
    }

    _buildSyncErrorMsg(): string {
        return "Sync error!";
    }

    constructor() {
        this.observers = <any>(new UMLPArrayList());
    }
}
GeneralErrorCollector["__class"] = "umlp.backendrte.common.GeneralErrorCollector";




import { GeneralErrorObserver } from './GeneralErrorObserver';


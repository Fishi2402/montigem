/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Channel } from '@src/functionstructure4fenix/Channel';
import { DoubleValue } from '@src/functionstructure4fenix/DoubleValue';
import { Function } from '@src/functionstructure4fenix/Function';
import { Port } from '@src/functionstructure4fenix/Port';
import { Value } from '@src/functionstructure4fenix/Value';
import { Channel4Display } from '@umlp/functiondisplay';
import { Channel4DisplayBuilder } from '@umlp/functiondisplay';
import { Function4Display } from '@umlp/functiondisplay';
import { Function4DisplayBuilder } from '@umlp/functiondisplay';
import { Port4Display } from '@umlp/functiondisplay';
import { Port4DisplayBuilder } from '@umlp/functiondisplay';
import { Component } from '@angular/core';
import { FunctionDetailTOP } from './FunctionDetailTOP';
import { StringHelper } from '@umlp/commonj2ts';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { FunctionDetailComponent, config } from '@src/gui/FunctionDetailComponent';
import { OnDestroy } from '@angular/core';
import { GemListUtil } from '@umlp/commonj2ts';
import { FunctionStructure4FenixManager } from '@src/functionstructure4fenix/FunctionStructure4FenixManager';
import ManyKeysMap from 'many-keys-map';
@Component({
    standalone: true,
    imports: [...config.imports],
    selector: config.selector,
    templateUrl: config.templateUrl,
    styles: config.styles,
    styleUrls: [...config.styleUrls]
})
export class FunctionDetail extends FunctionDetailComponent implements OnDestroy {
    public ngOnDestroy(): void {
        super.ngOnDestroy();
        ComponentObserverManager.removeObservers("gui.FunctionDetail" + this.uniqueIdentifier);
    }

    /**
     *
     * @param {Function} sf
     * @return {*}
     */
    public goToFunctionDetailsSub(sf: Function): (p1: void) => void {
        return <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(this.goToFunctionDetails(sf)));
    }

    /**
     *
     * @param {Function} f
     * @return {*}
     */
    public async goToFunctionDetailsSuper(f: Function): Promise<(p1: void) => void> {
        if (this.getF().getOwnerGem().isPresent()){
            return <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(this.goToFunctionDetails((await this.getF().getOwnerGem().get()))));
        } else {
            return <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(this.goToFunctionDetails(f)));
        }
    }

    public goToFunctionDetails(sf: Function): (p1: void) => void {
        return ($) => {
            this._router.navigate(["gui", "FunctionDetail", "" + sf.getGemId()]).then(() => { window.location.reload(); });;
        };
    }

    /**
     *
     * @param {Port} p
     * @param {String} __domainObs
     * @param {String} __statevarObs
     * @return {string}
     */
    public async getValueTextInValue(p: Port, __domainObs = this.getDomainObs('getValueTextInValue__update' + this.computeHash([p]), () => this.getValueTextInValue__update(p)), __statevarObs = this.getStateVarObs('getValueTextInValue__update' + this.computeHash([p]), () => this.getValueTextInValue__update(p))): Promise<string> {
        __domainObs(p, "Port");
        return await this.getPortStringValue(p,__domainObs, __statevarObs);
    }

    /**
     *
     * @param {Port} p
     * @param {String} __domainObs
     * @param {String} __statevarObs
     * @return {string}
     */
    public async getValueTextOutValue(p: Port, __domainObs = this.getDomainObs('getValueTextOutValue__update' + this.computeHash([p]), () => this.getValueTextOutValue__update(p)), __statevarObs = this.getStateVarObs('getValueTextOutValue__update' + this.computeHash([p]), () => this.getValueTextOutValue__update(p))): Promise<string> {
        __domainObs(p, "Port");
        return await this.getPortStringValue(p,__domainObs, __statevarObs);
    }

    private getPortStringValue__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getPortStringValue__synchronized(p: Port): string {
        if (!this.allVarsInited) {
            return undefined;
        }
        if (this.getPortStringValue__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getPortStringValue__SynchronizedResult.set([p.getGemId()], null);
            this.getPortStringValue__update(p);
        }
        return this.getPortStringValue__SynchronizedResult.get([p.getGemId()]);
    }

    public getPortStringValue__update(p: Port): void {

        Promise.resolve(this.getPortStringValue(p)).then(__v => this.getPortStringValue__SynchronizedResult.set([p.getGemId()], __v === undefined ? null : __v));
    }

    /*private*/ async getPortStringValue(p: Port, __domainObs = this.getDomainObs('getPortStringValue__update' + this.computeHash([p]), () => this.getPortStringValue__update(p)), __statevarObs = this.getStateVarObs('getPortStringValue__update' + this.computeHash([p]), () => this.getPortStringValue__update(p))): Promise<string> {
        __domainObs(p, "Port");
        if ((await GemListUtil.toArraySync(await __domainObs((await __domainObs((await p.getChannel()), "Channel").getFunctionStream()), "FunctionStream").getValueList())) == null){
            return null;
        }
        let res: string = "";
        {
            let array = (await GemListUtil.toArraySync(await __domainObs((await __domainObs((await p.getChannel()), "Channel").getFunctionStream()), "FunctionStream").getValueList()));
            for(let index = 0; index < array.length; index++) {
                let value = array[index];
                {
                    if (value != null && value instanceof <any>DoubleValue){
                        if (value != null && value instanceof <any>DoubleValue){
                            res += (<DoubleValue>value).getContent() + ", ";
                        }
                    }
                }
            }
        }
        return res.substring(0, res.length - 2);
    }

    /**
     *
     * @param {Function} function
     * @return {Function4Display}
     */
    public async transformData(__function: Function): Promise<Function4Display> {
        const functionMap: Map<Function, Function4Display> = <any>(new Map());
        const portMap: Map<Port, Port4Display> = <any>(new Map());
        const channelMap: Map<Channel, Channel4Display> = <any>(new Map());
        return await this.function2Function4Display(__function, functionMap, portMap, channelMap);
    }

    static counter: number = 0;

    /*private*/ async function2Function4Display(__function: Function, functionMap: Map<Function, Function4Display>, portMap: Map<Port, Port4Display>, channelMap: Map<Channel, Channel4Display>): Promise<Function4Display> {
        if (/* containsKey */functionMap.has(__function)){
            return /* get */functionMap.get(__function);
        } else if (__function == null){
            return null;
        } else {
            const function4Display: Function4DisplayBuilder = new Function4DisplayBuilder().name(__function.getName() == null ? "" : __function.getName());
            /* put */functionMap.set(__function, function4Display);
            const _in: Array<Port4Display> = <any>([]);
            for(let i: number = 0; i < /* size */(<number>(await GemListUtil.toArraySync((__function.getInList()))).length); i++) {{
                /* add */(_in.push((await this.port2Port4Display((await __function.getIn(i)), portMap, channelMap)))>0);
            };}
            function4Display.in(_in);
            const out: Array<Port4Display> = <any>([]);
            for(let i: number = 0; i < /* size */(<number>(await GemListUtil.toArraySync((await __function.getOutList()))).length); i++) {{
                /* add */(out.push((await this.port2Port4Display((await __function.getOut(i)), portMap, channelMap)))>0);
            };}
            function4Display.out(out);
            const subf: Array<Function4Display> = <any>([]);
            for(let i: number = 0; i < /* size */(<number>(await GemListUtil.toArraySync((await __function.getSubFunctionList()))).length); i++) {{
                /* add */(subf.push((await this.function2Function4Display((await __function.getSubFunction(i)), functionMap, portMap, channelMap)))>0);
            };}
            function4Display.subFunction(subf);
            function4Display.owner(await __function.getOwner() != null ? await this.function2Function4Display((await __function.getOwner()), functionMap, portMap, channelMap) : null);
            return /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(function4Display.build());
        }
    }

    public async port2Port4Display(port: Port, portMap: Map<Port, Port4Display>, channelMap: Map<Channel, Channel4Display>): Promise<Port4Display> {
        if (/* containsKey */portMap.has(port)){
            return /* get */portMap.get(port);
        } else {
            const port4Display: Port4DisplayBuilder = new Port4DisplayBuilder().name(port.getName() + FunctionDetail.counter++);
            /* put */portMap.set(port, port4Display);
            port4Display.channel4Display(await port.getChannel() != null ? await this.channel2Channel4Display((await port.getChannel()), portMap, channelMap) : null);
            return /* get */(v => { if (v == null) throw new Error('value is null'); return v; })(port4Display.build());
        }
    }

    public async channel2Channel4Display(channel: Channel, portMap: Map<Port, Port4Display>, channelMap: Map<Channel, Channel4Display>): Promise<Channel4Display> {
        if (/* containsKey */channelMap.has(channel)){
            return /* get */channelMap.get(channel);
        } else {
            const channel4Display: Channel4DisplayBuilder = new Channel4DisplayBuilder();
            const ports: Array<Port4Display> = <any>([]);
            for(let i: number = 0; i < /* size */(<number>(await GemListUtil.toArraySync((await channel.getPortList()))).length); i++) {{
                /* add */(ports.push(await this.port2Port4Display((await channel.getPort(i)), portMap, channelMap)));
            };}
            channel4Display.port4Display(ports);
            /* put */channelMap.set(channel, channel4Display);
            return channel4Display;
        }
    }

    constructor() {
        super();
    }
}
FunctionDetail["__class"] = "gui.FunctionDetail";
FunctionDetail["__interfaces"] = ["functionstructure4fenix.FunctionObserver"];





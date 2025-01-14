/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { DoubleValue } from 'functionstructure4fenix/DoubleValue';
import { Function } from 'functionstructure4fenix/Function';
import { Port } from 'functionstructure4fenix/Port';
import { Channel } from 'functionstructure4fenix/Channel';
import { Value } from 'functionstructure4fenix/Value';
import { Gem } from '@umlp/commonj2ts';
import { Random } from '@umlp/commonj2ts';
import { Component } from '@angular/core';
import { FunctionDetailTOP } from './FunctionDetailTOP';
import { StringHelper } from '@umlp/commonj2ts';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { FunctionDetailComponent, config } from 'gui/FunctionDetailComponent';
import { OnDestroy } from '@angular/core';
import { FunctionStructure4FenixManager } from 'functionstructure4fenix/FunctionStructure4FenixManager';
import ManyKeysMap from 'many-keys-map';
import {Function4Display, Channel4Display, Port4Display}  from '@umlp/functiondisplay';

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
        ComponentObserverManager.removeObservers("gui.FunctionDetail");
    }

  res: Function4Display;
  public override transformData(val_$_$0: Function): Function4Display {
    this.res = this.createDummyFunction4Display();
    //this.funktion2Function4Display(val_$_$0).then(v => this.res = v);
    console.error(this.res);
    return this.res;
  }

  public createDummyFunction4Display() : Function4Display {
    let a : Function4Display = {
      name: "a",
      in: [],
      out: [],
      subFunction: [],
      owner: undefined
    };
    let b : Function4Display = {
      name: "b",
      in: [],
      out: [],
      subFunction: [],
      owner: undefined
    };
    let function4Display : Function4Display = {
      name: "Dummy",
      in: [],
      out: [],
      subFunction: [a,b],
      owner: undefined
    };
    a.owner = function4Display;
    b.owner = function4Display;

    let ain : Port4Display = {
      name: "aIn",
      channel: undefined
    };
    a.in = [ain];
    let aout : Port4Display = {
      name: "aOut",
      channel: undefined
    };
    a.out = [aout];
    let bin : Port4Display = {
      name: "bIn",
      channel: undefined
    };
    let bin2 : Port4Display = {
      name: "bIn2",
      channel: undefined
    };
    b.in = [bin, bin2];
    let bout : Port4Display = {
      name: "bOut",
      channel: undefined
    };
    b.out = [bout];
    let outerIn : Port4Display = {
      name: "outerIn",
      channel: undefined
    };
    function4Display.in = [outerIn];
    let outerOut : Port4Display = {
      name: "outerOut",
      channel: undefined
    };
    function4Display.out = [outerOut];

    let oab : Channel4Display = {
      ports: [outerIn,ain, bin2]
    };
    outerIn.channel = oab;
    ain.channel = oab;
    bin2.channel = oab;
    let ab : Channel4Display = {
      ports: [aout,bin]
    };
    aout.channel = ab;
    bin.channel = ab;
    let bo : Channel4Display = {
      ports: [bout,outerOut]
    };
    bout.channel = bo;
    outerOut.channel = bo;


    return function4Display;
  }

  private functionMap = new Map<Function,Function4Display>;
  private portMap = new Map<Port,Port4Display>;
  private channelMap = new Map<Channel,Channel4Display>;

  public async funktion2Function4Display(funktion: Function): Promise<Function4Display> {
    if (this.functionMap.has(funktion)) {
      return this.functionMap.get(funktion);
    } else if (funktion === null) {
      return null;
    } else {
      let function4Display : Function4Display = {
        name: funktion.getName() == null ? "" : funktion.getName(),
        in: undefined,
        out: undefined,
        subFunction: undefined,
        owner: undefined
      };
      this.functionMap.set(funktion,function4Display);
      let _in = [];
      for (let i = 0; i<funktion.getInList().size();i++) {
        _in.push(await this.port2Port4Display(await funktion.getIn(i)));
      }
      function4Display.in = _in;
      let out = [];
      for (let i = 0; i<funktion.getOutList().size();i++) {
        out.push(await this.port2Port4Display(await funktion.getOut(i)));
      }
      function4Display.out = out;
      let subf = [];
      for (let i = 0; i<funktion.getSubFunctionList().size();i++) {
        subf.push(await this.funktion2Function4Display(await funktion.getSubFunction(i)));
      }
      function4Display.subFunction = subf;
      function4Display.owner = funktion.getOwner() ? await this.funktion2Function4Display(await funktion.getOwner()) : undefined;
      return function4Display;
    }
  }

  public async port2Port4Display(port: Port): Promise<Port4Display> {
    if(this.portMap.has(port)){
      return this.portMap.get(port);
    } else {
      let port4Display : Port4Display = {
        name: port.getName(),
        channel: port.getChannel ? await this.channel2Channel4Display(await port.getChannel()) : undefined
      };
      this.portMap.set(port,port4Display);
      return port4Display;
    }
  }

  public async channel2Channel4Display(channel: Channel): Promise<Channel4Display> {
    if(this.channelMap.has(channel)){
      return this.channelMap.get(channel);
    } else {
      let ports: Port[] = [];
      for (let i = 0; i<channel.getPortList().size();i++) {
        ports.push(await channel.getPort(i));
      }
      let channel4Display : Channel4Display = {
        ports: ports
      };
      this.channelMap.set(channel,channel4Display);
      return channel4Display;
    }
  }

    /**
     *
     * @param {Function} f
     * @return {*}
     */
    public async changeOwnerName(f: Function): Promise<(p1: void) => void> {
        return async ($) => {
            (await this.getF().getOwner()).setName$java_lang_String("TES31231");
        };
    }

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
     * @return {string}
     */
    public async getValueTextInValue(p: Port): Promise<string> {
        let __obs = this.getObs('getValueTextInValue__update', () => this.getValueTextInValue__update(p));
        __obs(p);
        return await this.getPortStringValue(p);
    }

    /**
     *
     * @param {Port} p
     * @return {string}
     */
    public async getValueTextOutValue(p: Port): Promise<string> {
        let __obs = this.getObs('getValueTextOutValue__update', () => this.getValueTextOutValue__update(p));
        __obs(p);
        return await this.getPortStringValue(p);
    }

    private getPortStringValue__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getPortStringValue__synchronized(p: Port): string {
        if (this.getPortStringValue__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getPortStringValue__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getPortStringValue__update(p));
        }
        return this.getPortStringValue__SynchronizedResult.get([p.getGemId()]);
    }

    public getPortStringValue__update(p: Port): void {
        Promise.resolve(this.getPortStringValue(p)).then(__v => this.getPortStringValue__SynchronizedResult.set([p.getGemId()], __v));
    }

    /*private*/ async getPortStringValue(p: Port): Promise<string> {
        let __obs = this.getObs('getPortStringValue__update', () => this.getPortStringValue__update(p));
        __obs(p);
        if (await __obs((await __obs((await p.getChannel())).getFunctionStream())).getValueList() == null){
            return null;
        }
        let res: string = "";
        for await(let value of (await __obs((await __obs((await p.getChannel())).getFunctionStream())).getValueList())) {
                        {
                if (value != null && value instanceof <any>DoubleValue){
                    if (value != null && value instanceof <any>DoubleValue){
                        res += (<DoubleValue>value).getContent() + ", ";
                    }
                }
            }
        }
        return res.substring(0, res.length - 2);
    }
}
FunctionDetail["__class"] = "gui.FunctionDetail";




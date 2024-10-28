/* Generated from Java with JSweet 3.2.0-UMLP-SNAPSHOT - http://www.jsweet.org */
import { Category } from '@src/functionstructure4fenix/Category';
import { Channel } from '@src/functionstructure4fenix/Channel';
import { Function } from '@src/functionstructure4fenix/Function';
import { FunctionStructure4FenixManager } from '@src/functionstructure4fenix/FunctionStructure4FenixManager';
import { Port } from '@src/functionstructure4fenix/Port';
import { Type } from '@src/functionstructure4fenix/Type';
import { StringHelper } from '@umlp/commonj2ts';
import { IUMLPList } from '@umlp/commonj2ts';
import { Directive } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { GemIdentifiable } from '@umlp/commonj2ts';
import ManyKeysMap from 'many-keys-map';
import {Function4Display, Channel4Display, Port4Display}  from '@umlp/functiondisplay';
@Directive()
export abstract class FunctionDetailTOP {

    protected router: Router;

    protected allInitedPromise: Promise<void>;
    protected waitAllInited(): Promise<void> {
        if (this.allInitedPromise === undefined) {
            this.allInitedPromise = Promise.resolve().then(() => this.wait_f__inited()).then(() => this.waitfRoutingId__inited());
        }
        return this.allInitedPromise;
    }

    protected getObs<T extends GemIdentifiable>(updateName: string, update: (() => void)): <T extends GemIdentifiable>(obj: T) => T {
        return (obj: T) => ComponentObserverManager.addObserver(obj, update.bind(this), updateName, "gui.FunctionDetail");
    }

    private async wait_f__inited(): Promise<void> {
        for (let counter = 0; counter < 100 && this._f == null; counter++) {
            await new Promise(r => setTimeout(r, 100));
        }
        if (this._f == null) {
            console.error('_f did not return a result after waiting for 10 seconds');
        }
    }
    _f: Function;

    private async waitfRoutingId__inited(): Promise<void> {
        for (let counter = 0; counter < 100 && this.fRoutingId == null; counter++) {
            await new Promise(r => setTimeout(r, 100));
        }
        if (this.fRoutingId == null) {
            console.error('fRoutingId did not return a result after waiting for 10 seconds');
        }
    }
    public fRoutingId: number;

    private getF__SynchronizedResult: Function = undefined;
    public getF__synchronized(): Function {
        if (this.getF__SynchronizedResult === undefined) {
            this.getF__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getF__update());
        }
        return this.getF__SynchronizedResult;
    }

    public getF__update(): void {
        Promise.resolve(this.getF()).then(__v => this.getF__SynchronizedResult = __v);
    }

    public getF(): Function {
        let __obs = this.getObs('getF__update', () => this.getF__update());
        return this._f;
    }

    public setF(val: Function) {
        this._f = val;
    }

    public transformData(val_$_$0: Function): Function4Display {
        return null;
    }

    private getFunctionDisplay2Data__SynchronizedResult: Function4Display = undefined;
    public getFunctionDisplay2Data__synchronized(): Function4Display {
        if (this.getFunctionDisplay2Data__SynchronizedResult === undefined) {
            this.getFunctionDisplay2Data__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getFunctionDisplay2Data__update());
        }
        return this.getFunctionDisplay2Data__SynchronizedResult;
    }

    public getFunctionDisplay2Data__update(): void {
        Promise.resolve(this.getFunctionDisplay2Data()).then(__v => this.getFunctionDisplay2Data__SynchronizedResult = __v);
    }

    public getFunctionDisplay2Data(): Function4Display {
        let __obs = this.getObs('getFunctionDisplay2Data__update', () => this.getFunctionDisplay2Data__update());
        if (false){
            return null;
        }
        const val_$: Function4Display = this.transformData(__obs(this.getF()));
        return val_$;
    }

    public async goToFunctionDetailsSuper(val_$_$0: Function): Promise<(p1: void) => void> {
        return <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(null));
    }

    private getOwningFunctionButtonLabel__SynchronizedResult: string = undefined;
    public getOwningFunctionButtonLabel__synchronized(): string {
        if (this.getOwningFunctionButtonLabel__SynchronizedResult === undefined) {
            this.getOwningFunctionButtonLabel__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getOwningFunctionButtonLabel__update());
        }
        return this.getOwningFunctionButtonLabel__SynchronizedResult;
    }

    public getOwningFunctionButtonLabel__update(): void {
        Promise.resolve(this.getOwningFunctionButtonLabel()).then(__v => this.getOwningFunctionButtonLabel__SynchronizedResult = __v);
    }

    public getOwningFunctionButtonLabel(): string {
        let __obs = this.getObs('getOwningFunctionButtonLabel__update', () => this.getOwningFunctionButtonLabel__update());
        if (false){
            return null;
        }
        const val_$: string = "Back To Owning Function";
        return val_$;
    }

    public async owningFunctionButtonLeftClick(event_$: void) {
        if (await this.goToFunctionDetailsSuper(this.getF()) != null){
            (target => (typeof target === 'function') ? target(event_$) : (<any>target).accept(event_$))((await this.goToFunctionDetailsSuper(this.getF())));
        }
    }

    private getGemText8Value__SynchronizedResult: string = undefined;
    public getGemText8Value__synchronized(): string {
        if (this.getGemText8Value__SynchronizedResult === undefined) {
            this.getGemText8Value__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText8Value__update());
        }
        return this.getGemText8Value__SynchronizedResult;
    }

    public getGemText8Value__update(): void {
        Promise.resolve(this.getGemText8Value()).then(__v => this.getGemText8Value__SynchronizedResult = __v);
    }

    public async getGemText8Value(): Promise<string> {
        let __obs = this.getObs('getGemText8Value__update', () => this.getGemText8Value__update());
        if (false || __obs(this.getF()) == null || __obs(await __obs(this.getF()).getOwner()) == null){
            return null;
        }
        const val_$: string = __obs((await __obs(this.getF()).getOwner())).getName();
        return val_$;
    }

    public async changeOwnerName(val_$_$0: Function): Promise<(p1: void) => void> {
        return <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(null));
    }

    private getGemButton9Label__SynchronizedResult: string = undefined;
    public getGemButton9Label__synchronized(): string {
        if (this.getGemButton9Label__SynchronizedResult === undefined) {
            this.getGemButton9Label__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemButton9Label__update());
        }
        return this.getGemButton9Label__SynchronizedResult;
    }

    public getGemButton9Label__update(): void {
        Promise.resolve(this.getGemButton9Label()).then(__v => this.getGemButton9Label__SynchronizedResult = __v);
    }

    public getGemButton9Label(): string {
        let __obs = this.getObs('getGemButton9Label__update', () => this.getGemButton9Label__update());
        if (false){
            return null;
        }
        const val_$: string = "Change Name";
        return val_$;
    }

    public async gemButton9LeftClick(event_$: void) {
        if (await this.changeOwnerName(this.getF()) != null){
            (target => (typeof target === 'function') ? target(event_$) : (<any>target).accept(event_$))((await this.changeOwnerName(this.getF())));
        }
    }

    private getGuard6Condition__SynchronizedResult: boolean = undefined;
    public getGuard6Condition__synchronized(): boolean {
        if (this.getGuard6Condition__SynchronizedResult === undefined) {
            this.getGuard6Condition__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGuard6Condition__update());
        }
        return this.getGuard6Condition__SynchronizedResult;
    }

    public getGuard6Condition__update(): void {
        Promise.resolve(this.getGuard6Condition()).then(__v => this.getGuard6Condition__SynchronizedResult = __v);
    }

    public async getGuard6Condition(): Promise<boolean> {
        let __obs = this.getObs('getGuard6Condition__update', () => this.getGuard6Condition__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(await __obs(this.getF()).getOwner()) != null;
    }

    private getGemText12Value__SynchronizedResult: string = undefined;
    public getGemText12Value__synchronized(): string {
        if (this.getGemText12Value__SynchronizedResult === undefined) {
            this.getGemText12Value__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText12Value__update());
        }
        return this.getGemText12Value__SynchronizedResult;
    }

    public getGemText12Value__update(): void {
        Promise.resolve(this.getGemText12Value()).then(__v => this.getGemText12Value__SynchronizedResult = __v);
    }

    public getGemText12Value(): string {
        let __obs = this.getObs('getGemText12Value__update', () => this.getGemText12Value__update());
        if (false){
            return null;
        }
        const val_$: string = "Owning Function: This is this outer-most function";
        return val_$;
    }

    private getGuard10Condition__SynchronizedResult: boolean = undefined;
    public getGuard10Condition__synchronized(): boolean {
        if (this.getGuard10Condition__SynchronizedResult === undefined) {
            this.getGuard10Condition__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGuard10Condition__update());
        }
        return this.getGuard10Condition__SynchronizedResult;
    }

    public getGuard10Condition__update(): void {
        Promise.resolve(this.getGuard10Condition()).then(__v => this.getGuard10Condition__SynchronizedResult = __v);
    }

    public async getGuard10Condition(): Promise<boolean> {
        let __obs = this.getObs('getGuard10Condition__update', () => this.getGuard10Condition__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(await __obs(this.getF()).getOwner()) == null;
    }

    private getGemText14Value__SynchronizedResult: string = undefined;
    public getGemText14Value__synchronized(): string {
        if (this.getGemText14Value__SynchronizedResult === undefined) {
            this.getGemText14Value__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText14Value__update());
        }
        return this.getGemText14Value__SynchronizedResult;
    }

    public getGemText14Value__update(): void {
        Promise.resolve(this.getGemText14Value()).then(__v => this.getGemText14Value__SynchronizedResult = __v);
    }

    public getGemText14Value(): string {
        let __obs = this.getObs('getGemText14Value__update', () => this.getGemText14Value__update());
        if (false){
            return null;
        }
        const val_$: string = "Ports";
        return val_$;
    }

    private getGemText14Size__SynchronizedResult: string = undefined;
    public getGemText14Size__synchronized(): string {
        if (this.getGemText14Size__SynchronizedResult === undefined) {
            this.getGemText14Size__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText14Size__update());
        }
        return this.getGemText14Size__SynchronizedResult;
    }

    public getGemText14Size__update(): void {
        Promise.resolve(this.getGemText14Size()).then(__v => this.getGemText14Size__SynchronizedResult = __v);
    }

    public getGemText14Size(): string {
        let __obs = this.getObs('getGemText14Size__update', () => this.getGemText14Size__update());
        if (false){
            return null;
        }
        const val_$: string = "large";
        return val_$;
    }

    private getGemText16Value__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemText16Value__synchronized(p: Port): string {
        if (this.getGemText16Value__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getGemText16Value__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemText16Value__update(p));
        }
        return this.getGemText16Value__SynchronizedResult.get([p.getGemId()]);
    }

    public getGemText16Value__update(p: Port): void {
        Promise.resolve(this.getGemText16Value(p)).then(__v => this.getGemText16Value__SynchronizedResult.set([p.getGemId()], __v));
    }

    public getGemText16Value(p: Port): string {
        let __obs = this.getObs('getGemText16Value__update', () => this.getGemText16Value__update(p));
        __obs(p);
        if (false || p == null){
            return null;
        }
        const val_$: string = "Input Port  " + p.getName();
        return val_$;
    }

    private getIt15Iterable__SynchronizedResult: IUMLPList<Port> = undefined;
    public getIt15Iterable__synchronized(): IUMLPList<Port> {
        if (this.getIt15Iterable__SynchronizedResult === undefined) {
            this.getIt15Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt15Iterable__update());
        }
        return this.getIt15Iterable__SynchronizedResult;
    }

    public getIt15Iterable__update(): void {
        Promise.resolve(this.getIt15Iterable()).then(__v => this.getIt15Iterable__SynchronizedResult = __v);
    }

    public getIt15Iterable(): IUMLPList<Port> {
        let __obs = this.getObs('getIt15Iterable__update', () => this.getIt15Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getInList();
    }

    private getGemText18Value__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemText18Value__synchronized(p: Port): string {
        if (this.getGemText18Value__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getGemText18Value__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemText18Value__update(p));
        }
        return this.getGemText18Value__SynchronizedResult.get([p.getGemId()]);
    }

    public getGemText18Value__update(p: Port): void {
        Promise.resolve(this.getGemText18Value(p)).then(__v => this.getGemText18Value__SynchronizedResult.set([p.getGemId()], __v));
    }

    public getGemText18Value(p: Port): string {
        let __obs = this.getObs('getGemText18Value__update', () => this.getGemText18Value__update(p));
        __obs(p);
        if (false || p == null){
            return null;
        }
        const val_$: string = "Output Port " + p.getName();
        return val_$;
    }

    private getIt17Iterable__SynchronizedResult: IUMLPList<Port> = undefined;
    public getIt17Iterable__synchronized(): IUMLPList<Port> {
        if (this.getIt17Iterable__SynchronizedResult === undefined) {
            this.getIt17Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt17Iterable__update());
        }
        return this.getIt17Iterable__SynchronizedResult;
    }

    public getIt17Iterable__update(): void {
        Promise.resolve(this.getIt17Iterable()).then(__v => this.getIt17Iterable__SynchronizedResult = __v);
    }

    public getIt17Iterable(): IUMLPList<Port> {
        let __obs = this.getObs('getIt17Iterable__update', () => this.getIt17Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getOutList();
    }

    private getPortsWidth__SynchronizedResult: string = undefined;
    public getPortsWidth__synchronized(): string {
        if (this.getPortsWidth__SynchronizedResult === undefined) {
            this.getPortsWidth__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getPortsWidth__update());
        }
        return this.getPortsWidth__SynchronizedResult;
    }

    public getPortsWidth__update(): void {
        Promise.resolve(this.getPortsWidth()).then(__v => this.getPortsWidth__SynchronizedResult = __v);
    }

    public getPortsWidth(): string {
        let __obs = this.getObs('getPortsWidth__update', () => this.getPortsWidth__update());
        if (false){
            return null;
        }
        const val_$: string = "auto";
        return val_$;
    }

    private getGemText19Value__SynchronizedResult: string = undefined;
    public getGemText19Value__synchronized(): string {
        if (this.getGemText19Value__SynchronizedResult === undefined) {
            this.getGemText19Value__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText19Value__update());
        }
        return this.getGemText19Value__SynchronizedResult;
    }

    public getGemText19Value__update(): void {
        Promise.resolve(this.getGemText19Value()).then(__v => this.getGemText19Value__SynchronizedResult = __v);
    }

    public getGemText19Value(): string {
        let __obs = this.getObs('getGemText19Value__update', () => this.getGemText19Value__update());
        if (false){
            return null;
        }
        const val_$: string = "Datatype";
        return val_$;
    }

    private getGemText19Size__SynchronizedResult: string = undefined;
    public getGemText19Size__synchronized(): string {
        if (this.getGemText19Size__SynchronizedResult === undefined) {
            this.getGemText19Size__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText19Size__update());
        }
        return this.getGemText19Size__SynchronizedResult;
    }

    public getGemText19Size__update(): void {
        Promise.resolve(this.getGemText19Size()).then(__v => this.getGemText19Size__SynchronizedResult = __v);
    }

    public getGemText19Size(): string {
        let __obs = this.getObs('getGemText19Size__update', () => this.getGemText19Size__update());
        if (false){
            return null;
        }
        const val_$: string = "large";
        return val_$;
    }

    private getGemText21Value__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemText21Value__synchronized(p: Port): string {
        if (this.getGemText21Value__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getGemText21Value__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemText21Value__update(p));
        }
        return this.getGemText21Value__SynchronizedResult.get([p.getGemId()]);
    }

    public getGemText21Value__update(p: Port): void {
        Promise.resolve(this.getGemText21Value(p)).then(__v => this.getGemText21Value__SynchronizedResult.set([p.getGemId()], __v));
    }

    public async getGemText21Value(p: Port): Promise<string> {
        let __obs = this.getObs('getGemText21Value__update', () => this.getGemText21Value__update(p));
        __obs(p);
        if (false || p == null || __obs(await p.getChannel()) == null || __obs(await __obs((await p.getChannel())).getCategory()) == null || __obs(await __obs((await __obs((await p.getChannel())).getCategory())).getType()) == null){
            return null;
        }
        const val_$: string = __obs((await __obs((await __obs((await p.getChannel())).getCategory())).getType())).getMctype();
        return val_$;
    }

    private getIt20Iterable__SynchronizedResult: IUMLPList<Port> = undefined;
    public getIt20Iterable__synchronized(): IUMLPList<Port> {
        if (this.getIt20Iterable__SynchronizedResult === undefined) {
            this.getIt20Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt20Iterable__update());
        }
        return this.getIt20Iterable__SynchronizedResult;
    }

    public getIt20Iterable__update(): void {
        Promise.resolve(this.getIt20Iterable()).then(__v => this.getIt20Iterable__SynchronizedResult = __v);
    }

    public getIt20Iterable(): IUMLPList<Port> {
        let __obs = this.getObs('getIt20Iterable__update', () => this.getIt20Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getInList();
    }

    private getGemText23Value__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemText23Value__synchronized(p: Port): string {
        if (this.getGemText23Value__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getGemText23Value__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemText23Value__update(p));
        }
        return this.getGemText23Value__SynchronizedResult.get([p.getGemId()]);
    }

    public getGemText23Value__update(p: Port): void {
        Promise.resolve(this.getGemText23Value(p)).then(__v => this.getGemText23Value__SynchronizedResult.set([p.getGemId()], __v));
    }

    public async getGemText23Value(p: Port): Promise<string> {
        let __obs = this.getObs('getGemText23Value__update', () => this.getGemText23Value__update(p));
        __obs(p);
        if (false || p == null || __obs(await p.getChannel()) == null || __obs(await __obs((await p.getChannel())).getCategory()) == null || __obs(await __obs((await __obs((await p.getChannel())).getCategory())).getType()) == null){
            return null;
        }
        const val_$: string = __obs((await __obs((await __obs((await p.getChannel())).getCategory())).getType())).getMctype();
        return val_$;
    }

    private getIt22Iterable__SynchronizedResult: IUMLPList<Port> = undefined;
    public getIt22Iterable__synchronized(): IUMLPList<Port> {
        if (this.getIt22Iterable__SynchronizedResult === undefined) {
            this.getIt22Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt22Iterable__update());
        }
        return this.getIt22Iterable__SynchronizedResult;
    }

    public getIt22Iterable__update(): void {
        Promise.resolve(this.getIt22Iterable()).then(__v => this.getIt22Iterable__SynchronizedResult = __v);
    }

    public getIt22Iterable(): IUMLPList<Port> {
        let __obs = this.getObs('getIt22Iterable__update', () => this.getIt22Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getOutList();
    }

    private getDatatypesWidth__SynchronizedResult: string = undefined;
    public getDatatypesWidth__synchronized(): string {
        if (this.getDatatypesWidth__SynchronizedResult === undefined) {
            this.getDatatypesWidth__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getDatatypesWidth__update());
        }
        return this.getDatatypesWidth__SynchronizedResult;
    }

    public getDatatypesWidth__update(): void {
        Promise.resolve(this.getDatatypesWidth()).then(__v => this.getDatatypesWidth__SynchronizedResult = __v);
    }

    public getDatatypesWidth(): string {
        let __obs = this.getObs('getDatatypesWidth__update', () => this.getDatatypesWidth__update());
        if (false){
            return null;
        }
        const val_$: string = "auto";
        return val_$;
    }

    private getGemText24Value__SynchronizedResult: string = undefined;
    public getGemText24Value__synchronized(): string {
        if (this.getGemText24Value__SynchronizedResult === undefined) {
            this.getGemText24Value__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText24Value__update());
        }
        return this.getGemText24Value__SynchronizedResult;
    }

    public getGemText24Value__update(): void {
        Promise.resolve(this.getGemText24Value()).then(__v => this.getGemText24Value__SynchronizedResult = __v);
    }

    public getGemText24Value(): string {
        let __obs = this.getObs('getGemText24Value__update', () => this.getGemText24Value__update());
        if (false){
            return null;
        }
        const val_$: string = "Values";
        return val_$;
    }

    private getGemText24Size__SynchronizedResult: string = undefined;
    public getGemText24Size__synchronized(): string {
        if (this.getGemText24Size__SynchronizedResult === undefined) {
            this.getGemText24Size__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText24Size__update());
        }
        return this.getGemText24Size__SynchronizedResult;
    }

    public getGemText24Size__update(): void {
        Promise.resolve(this.getGemText24Size()).then(__v => this.getGemText24Size__SynchronizedResult = __v);
    }

    public getGemText24Size(): string {
        let __obs = this.getObs('getGemText24Size__update', () => this.getGemText24Size__update());
        if (false){
            return null;
        }
        const val_$: string = "large";
        return val_$;
    }

    private getValueTextInValue__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getValueTextInValue__synchronized(p: Port): string {
        if (this.getValueTextInValue__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getValueTextInValue__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getValueTextInValue__update(p));
        }
        return this.getValueTextInValue__SynchronizedResult.get([p.getGemId()]);
    }

    public getValueTextInValue__update(p: Port): void {
        Promise.resolve(this.getValueTextInValue(p)).then(__v => this.getValueTextInValue__SynchronizedResult.set([p.getGemId()], __v));
    }

    public async getValueTextInValue(p: Port): Promise<string> {
        let __obs = this.getObs('getValueTextInValue__update', () => this.getValueTextInValue__update(p));
        __obs(p);
        if (false){
            return null;
        }
        const val_$: string = "";
        return val_$;
    }

    private getIt25Iterable__SynchronizedResult: IUMLPList<Port> = undefined;
    public getIt25Iterable__synchronized(): IUMLPList<Port> {
        if (this.getIt25Iterable__SynchronizedResult === undefined) {
            this.getIt25Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt25Iterable__update());
        }
        return this.getIt25Iterable__SynchronizedResult;
    }

    public getIt25Iterable__update(): void {
        Promise.resolve(this.getIt25Iterable()).then(__v => this.getIt25Iterable__SynchronizedResult = __v);
    }

    public getIt25Iterable(): IUMLPList<Port> {
        let __obs = this.getObs('getIt25Iterable__update', () => this.getIt25Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getInList();
    }

    private getValueTextOutValue__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getValueTextOutValue__synchronized(p: Port): string {
        if (this.getValueTextOutValue__SynchronizedResult.get([p.getGemId()]) === undefined) {
            this.getValueTextOutValue__SynchronizedResult.set([p.getGemId()], null);
            this.waitAllInited().then(_ => this.getValueTextOutValue__update(p));
        }
        return this.getValueTextOutValue__SynchronizedResult.get([p.getGemId()]);
    }

    public getValueTextOutValue__update(p: Port): void {
        Promise.resolve(this.getValueTextOutValue(p)).then(__v => this.getValueTextOutValue__SynchronizedResult.set([p.getGemId()], __v));
    }

    public async getValueTextOutValue(p: Port): Promise<string> {
        let __obs = this.getObs('getValueTextOutValue__update', () => this.getValueTextOutValue__update(p));
        __obs(p);
        if (false){
            return null;
        }
        const val_$: string = "?";
        return val_$;
    }

    private getIt27Iterable__SynchronizedResult: IUMLPList<Port> = undefined;
    public getIt27Iterable__synchronized(): IUMLPList<Port> {
        if (this.getIt27Iterable__SynchronizedResult === undefined) {
            this.getIt27Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt27Iterable__update());
        }
        return this.getIt27Iterable__SynchronizedResult;
    }

    public getIt27Iterable__update(): void {
        Promise.resolve(this.getIt27Iterable()).then(__v => this.getIt27Iterable__SynchronizedResult = __v);
    }

    public getIt27Iterable(): IUMLPList<Port> {
        let __obs = this.getObs('getIt27Iterable__update', () => this.getIt27Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getOutList();
    }

    private getChannelValuesWidth__SynchronizedResult: string = undefined;
    public getChannelValuesWidth__synchronized(): string {
        if (this.getChannelValuesWidth__SynchronizedResult === undefined) {
            this.getChannelValuesWidth__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getChannelValuesWidth__update());
        }
        return this.getChannelValuesWidth__SynchronizedResult;
    }

    public getChannelValuesWidth__update(): void {
        Promise.resolve(this.getChannelValuesWidth()).then(__v => this.getChannelValuesWidth__SynchronizedResult = __v);
    }

    public getChannelValuesWidth(): string {
        let __obs = this.getObs('getChannelValuesWidth__update', () => this.getChannelValuesWidth__update());
        if (false){
            return null;
        }
        const val_$: string = "auto";
        return val_$;
    }

    private getGemText29Value__SynchronizedResult: string = undefined;
    public getGemText29Value__synchronized(): string {
        if (this.getGemText29Value__SynchronizedResult === undefined) {
            this.getGemText29Value__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText29Value__update());
        }
        return this.getGemText29Value__SynchronizedResult;
    }

    public getGemText29Value__update(): void {
        Promise.resolve(this.getGemText29Value()).then(__v => this.getGemText29Value__SynchronizedResult = __v);
    }

    public getGemText29Value(): string {
        let __obs = this.getObs('getGemText29Value__update', () => this.getGemText29Value__update());
        if (false){
            return null;
        }
        const val_$: string = "Sub Functions";
        return val_$;
    }

    private getGemText29Size__SynchronizedResult: string = undefined;
    public getGemText29Size__synchronized(): string {
        if (this.getGemText29Size__SynchronizedResult === undefined) {
            this.getGemText29Size__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemText29Size__update());
        }
        return this.getGemText29Size__SynchronizedResult;
    }

    public getGemText29Size__update(): void {
        Promise.resolve(this.getGemText29Size()).then(__v => this.getGemText29Size__SynchronizedResult = __v);
    }

    public getGemText29Size(): string {
        let __obs = this.getObs('getGemText29Size__update', () => this.getGemText29Size__update());
        if (false){
            return null;
        }
        const val_$: string = "large";
        return val_$;
    }

    private getGemText32Value__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemText32Value__synchronized(sf: Function): string {
        if (this.getGemText32Value__SynchronizedResult.get([sf.getGemId()]) === undefined) {
            this.getGemText32Value__SynchronizedResult.set([sf.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemText32Value__update(sf));
        }
        return this.getGemText32Value__SynchronizedResult.get([sf.getGemId()]);
    }

    public getGemText32Value__update(sf: Function): void {
        Promise.resolve(this.getGemText32Value(sf)).then(__v => this.getGemText32Value__SynchronizedResult.set([sf.getGemId()], __v));
    }

    public getGemText32Value(sf: Function): string {
        let __obs = this.getObs('getGemText32Value__update', () => this.getGemText32Value__update(sf));
        __obs(sf);
        if (false || sf == null){
            return null;
        }
        const val_$: string = sf.getName();
        return val_$;
    }

    public goToFunctionDetailsSub(val_$_$0: Function): (p1: void) => void {
        return <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, arg0)})(null));
    }

    private getGemButton33Label__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemButton33Label__synchronized(sf: Function): string {
        if (this.getGemButton33Label__SynchronizedResult.get([sf.getGemId()]) === undefined) {
            this.getGemButton33Label__SynchronizedResult.set([sf.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemButton33Label__update(sf));
        }
        return this.getGemButton33Label__SynchronizedResult.get([sf.getGemId()]);
    }

    public getGemButton33Label__update(sf: Function): void {
        Promise.resolve(this.getGemButton33Label(sf)).then(__v => this.getGemButton33Label__SynchronizedResult.set([sf.getGemId()], __v));
    }

    public getGemButton33Label(sf: Function): string {
        let __obs = this.getObs('getGemButton33Label__update', () => this.getGemButton33Label__update(sf));
        __obs(sf);
        if (false){
            return null;
        }
        const val_$: string = "Details";
        return val_$;
    }

    public gemButton33LeftClick(event_$: void, sf: Function) {
        if (this.goToFunctionDetailsSub(sf) != null){
            (target => (typeof target === 'function') ? target(event_$) : (<any>target).accept(event_$))(this.goToFunctionDetailsSub(sf));
        }
    }

    private getGemRow31Width__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemRow31Width__synchronized(sf: Function): string {
        if (this.getGemRow31Width__SynchronizedResult.get([sf.getGemId()]) === undefined) {
            this.getGemRow31Width__SynchronizedResult.set([sf.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemRow31Width__update(sf));
        }
        return this.getGemRow31Width__SynchronizedResult.get([sf.getGemId()]);
    }

    public getGemRow31Width__update(sf: Function): void {
        Promise.resolve(this.getGemRow31Width(sf)).then(__v => this.getGemRow31Width__SynchronizedResult.set([sf.getGemId()], __v));
    }

    public getGemRow31Width(sf: Function): string {
        let __obs = this.getObs('getGemRow31Width__update', () => this.getGemRow31Width__update(sf));
        __obs(sf);
        if (false){
            return null;
        }
        const val_$: string = "auto";
        return val_$;
    }

    private getGemRow31RowGap__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemRow31RowGap__synchronized(sf: Function): string {
        if (this.getGemRow31RowGap__SynchronizedResult.get([sf.getGemId()]) === undefined) {
            this.getGemRow31RowGap__SynchronizedResult.set([sf.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemRow31RowGap__update(sf));
        }
        return this.getGemRow31RowGap__SynchronizedResult.get([sf.getGemId()]);
    }

    public getGemRow31RowGap__update(sf: Function): void {
        Promise.resolve(this.getGemRow31RowGap(sf)).then(__v => this.getGemRow31RowGap__SynchronizedResult.set([sf.getGemId()], __v));
    }

    public getGemRow31RowGap(sf: Function): string {
        let __obs = this.getObs('getGemRow31RowGap__update', () => this.getGemRow31RowGap__update(sf));
        __obs(sf);
        if (false){
            return null;
        }
        const val_$: string = "10px";
        return val_$;
    }

    private getGemRow31ColGap__SynchronizedResult: ManyKeysMap<any, string> = new ManyKeysMap()
    public getGemRow31ColGap__synchronized(sf: Function): string {
        if (this.getGemRow31ColGap__SynchronizedResult.get([sf.getGemId()]) === undefined) {
            this.getGemRow31ColGap__SynchronizedResult.set([sf.getGemId()], null);
            this.waitAllInited().then(_ => this.getGemRow31ColGap__update(sf));
        }
        return this.getGemRow31ColGap__SynchronizedResult.get([sf.getGemId()]);
    }

    public getGemRow31ColGap__update(sf: Function): void {
        Promise.resolve(this.getGemRow31ColGap(sf)).then(__v => this.getGemRow31ColGap__SynchronizedResult.set([sf.getGemId()], __v));
    }

    public getGemRow31ColGap(sf: Function): string {
        let __obs = this.getObs('getGemRow31ColGap__update', () => this.getGemRow31ColGap__update(sf));
        __obs(sf);
        if (false){
            return null;
        }
        const val_$: string = "10px";
        return val_$;
    }

    private getIt30Iterable__SynchronizedResult: IUMLPList<Function> = undefined;
    public getIt30Iterable__synchronized(): IUMLPList<Function> {
        if (this.getIt30Iterable__SynchronizedResult === undefined) {
            this.getIt30Iterable__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getIt30Iterable__update());
        }
        return this.getIt30Iterable__SynchronizedResult;
    }

    public getIt30Iterable__update(): void {
        Promise.resolve(this.getIt30Iterable()).then(__v => this.getIt30Iterable__SynchronizedResult = __v);
    }

    public getIt30Iterable(): IUMLPList<Function> {
        let __obs = this.getObs('getIt30Iterable__update', () => this.getIt30Iterable__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        return __obs(this.getF()).getSubFunctionList();
    }

    private getSubFunctionsWidth__SynchronizedResult: string = undefined;
    public getSubFunctionsWidth__synchronized(): string {
        if (this.getSubFunctionsWidth__SynchronizedResult === undefined) {
            this.getSubFunctionsWidth__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getSubFunctionsWidth__update());
        }
        return this.getSubFunctionsWidth__SynchronizedResult;
    }

    public getSubFunctionsWidth__update(): void {
        Promise.resolve(this.getSubFunctionsWidth()).then(__v => this.getSubFunctionsWidth__SynchronizedResult = __v);
    }

    public getSubFunctionsWidth(): string {
        let __obs = this.getObs('getSubFunctionsWidth__update', () => this.getSubFunctionsWidth__update());
        if (false){
            return null;
        }
        const val_$: string = "auto";
        return val_$;
    }

    private getGemRow13Width__SynchronizedResult: string = undefined;
    public getGemRow13Width__synchronized(): string {
        if (this.getGemRow13Width__SynchronizedResult === undefined) {
            this.getGemRow13Width__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemRow13Width__update());
        }
        return this.getGemRow13Width__SynchronizedResult;
    }

    public getGemRow13Width__update(): void {
        Promise.resolve(this.getGemRow13Width()).then(__v => this.getGemRow13Width__SynchronizedResult = __v);
    }

    public getGemRow13Width(): string {
        let __obs = this.getObs('getGemRow13Width__update', () => this.getGemRow13Width__update());
        if (false){
            return null;
        }
        const val_$: string = "100%";
        return val_$;
    }

    private getGemRow13ColGap__SynchronizedResult: string = undefined;
    public getGemRow13ColGap__synchronized(): string {
        if (this.getGemRow13ColGap__SynchronizedResult === undefined) {
            this.getGemRow13ColGap__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemRow13ColGap__update());
        }
        return this.getGemRow13ColGap__SynchronizedResult;
    }

    public getGemRow13ColGap__update(): void {
        Promise.resolve(this.getGemRow13ColGap()).then(__v => this.getGemRow13ColGap__SynchronizedResult = __v);
    }

    public getGemRow13ColGap(): string {
        let __obs = this.getObs('getGemRow13ColGap__update', () => this.getGemRow13ColGap__update());
        if (false){
            return null;
        }
        const val_$: string = "10px";
        return val_$;
    }

    private getGemColumn5Width__SynchronizedResult: string = undefined;
    public getGemColumn5Width__synchronized(): string {
        if (this.getGemColumn5Width__SynchronizedResult === undefined) {
            this.getGemColumn5Width__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemColumn5Width__update());
        }
        return this.getGemColumn5Width__SynchronizedResult;
    }

    public getGemColumn5Width__update(): void {
        Promise.resolve(this.getGemColumn5Width()).then(__v => this.getGemColumn5Width__SynchronizedResult = __v);
    }

    public getGemColumn5Width(): string {
        let __obs = this.getObs('getGemColumn5Width__update', () => this.getGemColumn5Width__update());
        if (false){
            return null;
        }
        const val_$: string = "100%";
        return val_$;
    }

    private getGemCard4Title__SynchronizedResult: string = undefined;
    public getGemCard4Title__synchronized(): string {
        if (this.getGemCard4Title__SynchronizedResult === undefined) {
            this.getGemCard4Title__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemCard4Title__update());
        }
        return this.getGemCard4Title__SynchronizedResult;
    }

    public getGemCard4Title__update(): void {
        Promise.resolve(this.getGemCard4Title()).then(__v => this.getGemCard4Title__SynchronizedResult = __v);
    }

    public getGemCard4Title(): string {
        let __obs = this.getObs('getGemCard4Title__update', () => this.getGemCard4Title__update());
        if (false || __obs(this.getF()) == null){
            return null;
        }
        const val_$: string = __obs(this.getF()).getName();
        return val_$;
    }

    private getGemColumn3Width__SynchronizedResult: string = undefined;
    public getGemColumn3Width__synchronized(): string {
        if (this.getGemColumn3Width__SynchronizedResult === undefined) {
            this.getGemColumn3Width__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemColumn3Width__update());
        }
        return this.getGemColumn3Width__SynchronizedResult;
    }

    public getGemColumn3Width__update(): void {
        Promise.resolve(this.getGemColumn3Width()).then(__v => this.getGemColumn3Width__SynchronizedResult = __v);
    }

    public getGemColumn3Width(): string {
        let __obs = this.getObs('getGemColumn3Width__update', () => this.getGemColumn3Width__update());
        if (false){
            return null;
        }
        const val_$: string = "100%";
        return val_$;
    }

    private getGemRow0Width__SynchronizedResult: string = undefined;
    public getGemRow0Width__synchronized(): string {
        if (this.getGemRow0Width__SynchronizedResult === undefined) {
            this.getGemRow0Width__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemRow0Width__update());
        }
        return this.getGemRow0Width__SynchronizedResult;
    }

    public getGemRow0Width__update(): void {
        Promise.resolve(this.getGemRow0Width()).then(__v => this.getGemRow0Width__SynchronizedResult = __v);
    }

    public getGemRow0Width(): string {
        let __obs = this.getObs('getGemRow0Width__update', () => this.getGemRow0Width__update());
        if (false){
            return null;
        }
        const val_$: string = "100%";
        return val_$;
    }

    private getGemRow0ColGap__SynchronizedResult: string = undefined;
    public getGemRow0ColGap__synchronized(): string {
        if (this.getGemRow0ColGap__SynchronizedResult === undefined) {
            this.getGemRow0ColGap__SynchronizedResult = null;
            this.waitAllInited().then(_ => this.getGemRow0ColGap__update());
        }
        return this.getGemRow0ColGap__SynchronizedResult;
    }

    public getGemRow0ColGap__update(): void {
        Promise.resolve(this.getGemRow0ColGap()).then(__v => this.getGemRow0ColGap__SynchronizedResult = __v);
    }

    public getGemRow0ColGap(): string {
        let __obs = this.getObs('getGemRow0ColGap__update', () => this.getGemRow0ColGap__update());
        if (false){
            return null;
        }
        const val_$: string = "10px";
        return val_$;
    }

    async initIt30Iterable() {
        for await(let sf of (this.getF().getSubFunctionList())) {
                        {
            }
        }
    }

    public initIt30Iterable_15() {
        this.initIt30Iterable();
    }

    async initIt27Iterable() {
        for await(let p of (this.getF().getOutList())) {
                        {
                this.initIt30Iterable_15();
            }
        }
    }

    public initIt27Iterable_14() {
        this.initIt27Iterable();
    }

    async initIt25Iterable() {
        for await(let p of (this.getF().getInList())) {
                        {
                this.initIt27Iterable_14();
            }
        }
    }

    public initIt25Iterable_13() {
        this.initIt25Iterable();
    }

    public initGemText23Value_10(p: Port) {
        this.initIt25Iterable_13();
    }

    public initGemText23Value_11(p: Port) {
        this.initGemText23Value_10(p);
    }

    public initGemText23Value_12(p: Port) {
        this.initGemText23Value_11(p);
    }

    async initIt22Iterable() {
        for await(let p of (this.getF().getOutList())) {
                        {
                this.initGemText23Value_12(p);
            }
        }
    }

    public initIt22Iterable_9() {
        this.initIt22Iterable();
    }

    public initGemText21Value_6(p: Port) {
        this.initIt22Iterable_9();
    }

    public initGemText21Value_7(p: Port) {
        this.initGemText21Value_6(p);
    }

    public initGemText21Value_8(p: Port) {
        this.initGemText21Value_7(p);
    }

    async initIt20Iterable() {
        for await(let p of (this.getF().getInList())) {
                        {
                this.initGemText21Value_8(p);
            }
        }
    }

    public initIt20Iterable_5() {
        this.initIt20Iterable();
    }

    async initIt17Iterable() {
        for await(let p of (this.getF().getOutList())) {
                        {
                this.initIt20Iterable_5();
            }
        }
    }

    public initIt17Iterable_4() {
        this.initIt17Iterable();
    }

    async initIt15Iterable() {
        for await(let p of (this.getF().getInList())) {
                        {
                this.initIt17Iterable_4();
            }
        }
    }

    public initIt15Iterable_3() {
        this.initIt15Iterable();
    }

    public initGuard10Condition_2() {
        this.initIt15Iterable_3();
    }

    public initGemText8Value_1() {
        this.initGuard10Condition_2();
    }

    public initGuard6Condition_0() {
        this.initGemText8Value_1();
    }

    async initF() {
        if (this.fRoutingId != null){
            this._f = await FunctionStructure4FenixManager.getFunctionSure(this.fRoutingId);
        } else {
            this._f = await FunctionStructure4FenixManager.functionBuilder();
        }
        this.initGuard6Condition_0();
    }

    public init() {
        this.initF();
    }

    public destroy() {
    }


}
FunctionDetailTOP["__class"] = "gui.FunctionDetailTOP";



import {Subject, BehaviorSubject} from 'rxjs';
import {Injectable, Injector} from '@angular/core';

// @Output does not work with dynamic components => global events

@Injectable({providedIn: 'root',})
export class InspectorService {
  protected inspectSubject: Subject<{ type: any, inputs: any }> = new Subject();

  public inspectObservable() {
    return this.inspectSubject.asObservable();
  }

  public emitInspect(data: any) {
    this.inspectSubject.next(data);
  }

  protected newElementRequester: Subject<((clazz: any, inputs: any) => void)> = new Subject();

  public newElementRequesterObservable() {
    return this.newElementRequester.asObservable();
  }


  public requestNewElem(callback: ((clazz: any, inputs: ()=>any) => void)) {
    this.newElementRequester.next(callback);
  }

  public showControls: BehaviorSubject<boolean> = new BehaviorSubject(true);

}


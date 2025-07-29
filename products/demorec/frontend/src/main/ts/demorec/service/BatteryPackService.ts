import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from "rxjs";
import { BatteryPack } from 'demorec/BatteryPack';

@Injectable({
  providedIn: 'root'
})
export class BatteryPackService {

  constructor() { }

  public currentPack : BehaviorSubject<BatteryPack> = new BehaviorSubject<BatteryPack>(null)

  public getBatteryPack() : BatteryPack {
    return this.currentPack.getValue();
  }

  public setBatteryPack(newPack : BatteryPack) {
    this.currentPack.next(newPack)
  }

  public addObserverFunction(updaterFunction: (x : BatteryPack) => void) : void {
    this.currentPack.subscribe(x => updaterFunction(x));
  }

}

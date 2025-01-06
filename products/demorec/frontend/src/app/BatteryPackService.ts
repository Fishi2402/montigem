import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from "node_modules/rxjs";
import { BatteryPack } from 'demorec/BatteryPack';

@Injectable({
  providedIn: 'root'
})
export class BatteryPackService {

  constructor() { }

  public currentPack : BehaviorSubject<BatteryPack> = new BehaviorSubject<BatteryPack>(null)

  public updateBatteryPack(newPack : BatteryPack) {
    this.currentPack.next(newPack)
  }

}

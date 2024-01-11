/* (c) https://github.com/MontiCore/monticore */

import {RoomStatsComponent,config} from '@src/gui/rooms/RoomStatsComponent';

import {RoomObserver} from '@src/dtcockpit/RoomObserver';
import {Room} from '@src/dtcockpit/Room';
import {NotificationScope} from '@umlp/common';
import { Component } from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class RoomStats extends RoomStatsComponent {
  temp4304 = "? °C";
  temp4315 = "? °C";
  temp4312 = "? °C";

  public override init(): void {
    setTimeout(() => {
      if (this.getStats() == null) {
        return;
      }
      for (let i = 0; i < this.getStats().ids.length; i++) {
        this.getStats().get(i).then(room => {
          if(room.getRoomNumber() == 4304){
            room.addObserver(new class extends RoomObserver {
              constructor(protected inst: RoomStats) {
                super();
              }
              public override notifySetTemperature (room: Room,oldValue: number,o: number): void {
                this.inst.temp4304 = o.toFixed(2) + " °C";
              }
              public override getScope(){
                return NotificationScope.MINIMAL;
              }
            }(this))
          }

          if(room.getRoomNumber() == 4315){
            room.addObserver(new class extends RoomObserver {
              constructor(protected inst: RoomStats) {
                super();
              }
              public override notifySetTemperature (room: Room,oldValue: number,o: number): void {
                this.inst.temp4315 = o.toFixed(2) + " °C";
              }
              public override getScope(){
                return NotificationScope.MINIMAL;
              }
            }(this))
          }

          if(room.getRoomNumber() == 4312){
            room.addObserver(new class extends RoomObserver {
              constructor(protected inst: RoomStats) {
                super();
              }
              public override notifySetTemperature (room: Room,oldValue: number,o: number): void {
                this.inst.temp4312 = o.toFixed(2) + " °C";
              }
              public override getScope(){
                return NotificationScope.MINIMAL;
              }
            }(this))
          }
        })
      }
    }, 2000);

  }

  public override getInfoLine4304BotInfo (): string {
    return this.temp4304;
  }

  public override getInfoLine4315BotInfo (): string {
    return this.temp4315;
  }

  public override getInfoLine4312BotInfo (): string {
    return this.temp4312;
  }

}

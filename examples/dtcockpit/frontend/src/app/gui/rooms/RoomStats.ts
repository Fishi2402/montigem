/* (c) https://github.com/MontiCore/monticore */

import { DtcockpitManager } from '@src/dtcockpit/DtcockpitManager';
import { Project } from '@src/dtcockpit/Project';
import { RoomStatistic } from '@src/dtcockpit/RoomStatistic';
import { RoomStatsTOP } from '@src/gui/rooms/RoomStatsTOP';

export class RoomStats extends RoomStatsTOP {

  public override init(): void {
    setTimeout(() => console.log(this.getStats()), 2000);
  }
}

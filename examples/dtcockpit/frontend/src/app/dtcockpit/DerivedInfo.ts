
import { DerivedInfoTOP } from '@src/dtcockpit/DerivedInfoTOP';
import { Meeting } from '@src/dtcockpit/Meeting';
import { Gem, GemList } from "@umlp/common";

import { MessageOfTheDay } from '@src/dtcockpit/MessageOfTheDay';
import { DerivedInfo_calculateSomeNumber } from '@src/dtcockpit/commands/DerivedInfo_calculateSomeNumber';
import { DerivedInfo_calculateSomeString } from '@src/dtcockpit/commands/DerivedInfo_calculateSomeString';
import { DerivedInfo_calculateMonthlyMeetings } from '@src/dtcockpit/commands/DerivedInfo_calculateMonthlyMeetings';
import { DerivedInfo_calculateManyStrings } from '@src/dtcockpit/commands/DerivedInfo_calculateManyStrings';
import { DerivedInfo_calculateImportantMeeting } from '@src/dtcockpit/commands/DerivedInfo_calculateImportantMeeting';
import { DerivedInfo_calculateRandomMeeting } from '@src/dtcockpit/commands/DerivedInfo_calculateRandomMeeting';


import { CommandManager } from '@umlp/common';


export class DerivedInfo extends DerivedInfoTOP {

  public getSomeNumber (): number {
    CommandManager.executeCommand(new DerivedInfo_calculateSomeNumber(this.gemId)).then(res =>
      {
        return res.asSimpleResult().get();
      });
    return 0;
  }

  public getSomeString (): string {
    CommandManager.executeCommand(new DerivedInfo_calculateSomeString(this.gemId)).then(res =>
      {
        return res.asSimpleResult().get();
      });
    return "";
  }

  public getMonthlyMeetingsDerivedGemList (): GemList<Meeting> {
    CommandManager.executeCommand(new DerivedInfo_calculateMonthlyMeetings(this.gemId)).then(res =>
      {
        return res.asListResult().get();
      });
    return null;
  }

  public getManyStringsList (): Array<string> {
    CommandManager.executeCommand(new DerivedInfo_calculateManyStrings(this.gemId)).then(res =>
      {
        return res.asListResult().get();
      });
    return null;
  }

  public getImportantMeetingDerivedGem (): Gem<Meeting> {
    CommandManager.executeCommand(new DerivedInfo_calculateImportantMeeting(this.gemId)).then(res =>
      {
        return res.asSimpleResult().get();
      });
    return null;
  }

  public getRandomMeetingDerivedGem (): Gem<Meeting> {
    CommandManager.executeCommand(new DerivedInfo_calculateRandomMeeting(this.gemId)).then(res =>
      {
        return res.asSimpleResult().get();
      });
    return null;
  }


}


import { DerivedInfoClientImplTOP } from '@src/dtcockpit/DerivedInfoClientImplTOP';
import { Meeting } from '@src/dtcockpit/Meeting';

import { MessageOfTheDay } from '@src/dtcockpit/MessageOfTheDay';


import { CommandManager } from '@umlp/common';
import { DerivedInfo_RandomMSG_get } from '@src/dtcockpit/commands/derivedclass/DerivedInfo_RandomMSG_get';
import { DerivedInfo_ManyStrings_get } from '@src/dtcockpit/commands/derivedclass/DerivedInfo_ManyStrings_get';
import { DerivedInfo_MonthlyMeetings_get } from '@src/dtcockpit/commands/derivedclass/DerivedInfo_MonthlyMeetings_get';


export class DerivedInfoClientImpl extends DerivedInfoClientImplTOP {


  constructor() {
    super();
    this.init();
  }

}

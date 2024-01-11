/* (c) https://github.com/MontiCore/monticore */

import { CityInfo } from '@src/hardy/flensburg/CityInfo';
import { Person } from '@src/hardy/flensburg/Person';
import {CommandManager} from "@umlp/common";
import {Transaction} from "@src/hardy/flensburg/Transaction";
import {MonthlyReportClientImplTOP} from "@src/hardy/flensburg/MonthlyReportClientImplTOP";

export class MonthlyReportClientImpl extends MonthlyReportClientImplTOP {


  getMonthlyTransaction(): Array<Transaction> {
    return undefined;
  }

  sizeMonthlyTransaction(): number {
    return 0;
  }


}

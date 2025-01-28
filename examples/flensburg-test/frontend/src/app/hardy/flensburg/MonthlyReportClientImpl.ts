/* (c) https://github.com/MontiCore/monticore */

import { CityInfo } from 'hardy/flensburg/CityInfo';
import { Person } from 'hardy/flensburg/Person';
import {CommandManager} from "@umlp/common";
import {Transaction} from "hardy/flensburg/Transaction";
import {MonthlyReportClientImplTOP} from "hardy/flensburg/MonthlyReportClientImplTOP";

export class MonthlyReportClientImpl extends MonthlyReportClientImplTOP {


  getMonthlyTransaction(): Array<Transaction> {
    return undefined;
  }

  sizeMonthlyTransaction(): number {
    return 0;
  }


}

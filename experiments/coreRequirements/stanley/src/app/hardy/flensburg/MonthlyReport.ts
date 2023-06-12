/* (c) https://github.com/MontiCore/monticore */

import { MonthlyReportTOP } from '@src/hardy/flensburg/MonthlyReportTOP';
import { Transaction } from '@src/hardy/flensburg/Transaction';

export class MonthlyReport extends MonthlyReportTOP {
  protected init(): void {
  }

  getMonth(): number {
    return 0;
  }
  getBalanceChange(): number {
    return 0;
  }
  sizeMonthlyTransaction(): number {
    return 0;
  }
  getMonthlyTransaction(): Array<Transaction> {
    return [];
  }
}
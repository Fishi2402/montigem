import { FlensburgManager } from "@src/hardy/flensburg/FlensburgManager";
import { MonthlyReportTOP } from "@src/hardy/flensburg/MonthlyReportTOP";
import { Transaction } from "@src/hardy/flensburg/Transaction";
import { TransactionGemList } from "@src/hardy/flensburg/gemlists/TransactionGemList";
import { GemList } from "@umlp/common";

export class MonthlyReport extends MonthlyReportTOP {

  protected transactions: GemList<Transaction> = new TransactionGemList([]);

  public constructor() {
    super();
    this.init();
  }

  public async init() {
    this.transactions = await FlensburgManager.getTransactionList();
  }

  /**
   * Month of the report. Set when a monthly report is requested. The value is from 0 (January) to
   * 11 (December)
   */
  protected month: number = 0;

  public getMonth(): number {
    return this.month;
  }

  public setMonth(month: number): void {
    this.month = month;
  }

  /**
   * @return Total balance change for the given month.
   */
  public getBalanceChange(): number {
    let change: number = 0;
    for (const t of this.getMonthlyTransaction()) {
      change += t.getAmount();
    }
    return change;
  }

  /**
   * @return Amount of transactions for the given month.
   */
  public sizeMonthlyTransaction(): number {
    return this.getMonthlyTransaction().length;
  }

  /**
   * @return List of transactions for the given month.
   */
  public getMonthlyTransaction(): Transaction[] {
    let monthlyTransactions: Transaction[] = [];
    for (const t of this.transactions) {
      if (t.getDate().getMonth() === this.getMonth()) {
        monthlyTransactions.push(t);
      }
    }
    return monthlyTransactions;
  }

}

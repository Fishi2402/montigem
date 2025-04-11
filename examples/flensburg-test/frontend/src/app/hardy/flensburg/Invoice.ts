import { InvoiceTOP } from "hardy/flensburg/InvoiceTOP";

export class Invoice extends InvoiceTOP {

  /**
   * @return Sum of invoice transactions.
   */
  public override getSum(): number {
    return 0;
  }

}

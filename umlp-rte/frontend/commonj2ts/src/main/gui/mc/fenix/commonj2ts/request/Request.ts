import {Command} from '../command/Command';

/**
 * @deprecated CBu: I think this class should be moved to package command/Request?
 */
export abstract class Request<T> extends Command<T> {

  private filter: string;

  public constructor() {
    super()
  }

  async __RequestConstructor(commandType: string, filter?: string) {
    await super.__CommandConstructor(commandType);
    this.filter = filter;
  }

  public getFilter(): string {
    return this.filter;
  }

  /**
   *
   * @return {boolean}
   */
  public override isRequest(): boolean {
    return true;
  }
}

Request["__class"] = "umlp.backendrte.request.Request";




import {Command} from '../command/Command';

export abstract class Request<T> extends Command<T> {

  private filter: string;

  public constructor(commandType: string, filter?: string) {
    super(commandType);
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




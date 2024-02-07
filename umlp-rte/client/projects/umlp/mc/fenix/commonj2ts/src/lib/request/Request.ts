import {Command} from '../command/Command';

export abstract class Request<T> extends Command<T> {
  constructor(commandType: string) {
    super(commandType);
  }

  /**
   *
   * @return {boolean}
   */
  public isRequest(): boolean {
    return true;
  }
}

Request["__class"] = "umlp.backendrte.request.Request";




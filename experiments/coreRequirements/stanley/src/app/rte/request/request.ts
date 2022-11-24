import {Command} from "../command/command";

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


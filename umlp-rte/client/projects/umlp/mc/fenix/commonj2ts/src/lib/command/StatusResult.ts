/* (c) https://github.com/MontiCore/monticore */
import {CommandResult} from './CommandResult';

export class StatusResult extends CommandResult<void> {
  successful: boolean;

  constructor(id: number, successful: boolean) {
    super(id, "java.lang.Void");
    if (this.successful === undefined) {
      this.successful = false;
    }
    this.successful = successful;
  }

  public static ok(id: number): StatusResult {
    return new StatusResult(id, true);
  }

  public static error(id: number): StatusResult {
    return new StatusResult(id, false);
  }

  public async wasSuccessful(): Promise<boolean> {
    return this.isSuccessful();
  }

  public async isSuccessful(): Promise<boolean> {
    return Promise.resolve(this.successful);
  }

  /**
   *
   * @return {boolean}
   */
  public override isStatusResult(): boolean {
    return true;
  }
}

StatusResult["__class"] = "umlp.backendrte.command.StatusResult";




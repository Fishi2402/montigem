/* (c) https://github.com/MontiCore/monticore */
import {CommandResult} from './CommandResult';

export class StatusResult extends CommandResult<void> {
  successful: boolean;

  constructor() {
    super()
  }

  public async __StatusResultConstrcutor(id: number, successful: boolean) : Promise<StatusResult> {
    await super.__CommandResultConstructor(id, "java.lang.Void");
    if (this.successful === undefined) {
      this.successful = false;
    }
    this.successful = successful;
    return this;
  }

  public static async ok(id: number): Promise<StatusResult> {
    return await new StatusResult().__StatusResultConstrcutor(id, true);
  }

  public static async error(id: number): Promise<StatusResult> {
    return await new StatusResult().__StatusResultConstrcutor(id, false);
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




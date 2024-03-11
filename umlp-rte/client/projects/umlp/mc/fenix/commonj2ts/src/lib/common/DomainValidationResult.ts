/* (c) https://github.com/MontiCore/monticore */

export abstract class DomainValidationResult {
  protected gemId: number;

  public DomainValidationResult(gemId?: number) : DomainValidationResult {
    this.gemId = gemId;
    return this;
  }

  public abstract hasErrors(): boolean;

  public abstract getDomainType(): string;

  public getGemId(): number {
    return this.gemId;
  }

  public hasGemId(): boolean{
    return this.gemId != null;
  }

  public setGemId(gemId: number) {
    this.gemId = gemId;
  }
}

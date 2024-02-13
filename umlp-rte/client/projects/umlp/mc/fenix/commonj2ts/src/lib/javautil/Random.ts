export class Random {

  max: number = 2 ^ 32;

  public nextInt(bound?: number): number {
    if (bound === undefined || bound === null || bound == undefined || bound == null) {
      bound = this.max;
    }
    return Math.floor(Math.random() * bound);
  }
}

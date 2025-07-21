export class IdUtil {
  public static getUniqueId(): string {
    return Math.random().toString(16).slice(2);
  }
}

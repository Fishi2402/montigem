export class Log {
  public static warn(msg: string): void {
    console.log("[WARN] " + msg);
  }

  public static trace(msg: string, logger: string): void {
    console.log("[TRACE] " + logger + " : " + msg);
  }

  static error(msg: string) {
    console.log("[ERROR] " + msg);
  }

  public static info(msg: string, logName: string) {
    console.log("[INFO]  " + logName + " : " + msg)
  }
}

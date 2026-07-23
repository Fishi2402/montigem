const DEFAULT_LOG_MSG  : string = "[se_rwth.Log]";

export class Log {

  public static error(msg: string) {
    console.error(DEFAULT_LOG_MSG,msg,Log.getFirstStackTraceFrameOpt());
  }

  public static warn(msg: string): void {
    console.warn(DEFAULT_LOG_MSG,msg,Log.getFirstStackTraceFrameOpt());
  }
  public static info(msg: string, logName: string) {
    console.info(DEFAULT_LOG_MSG,logName + " : " + msg,Log.getFirstStackTraceFrameOpt())
  }

  public static debug(msg: string, logName: string) {
    if (!Log.checkForDebugLevel()) return;
    console.debug(DEFAULT_LOG_MSG,logName + " : " + msg,Log.getFirstStackTraceFrameOpt())
  }

  public static trace(msg: string, logger: string): void {
    if (!Log.checkForDebugLevel()) return;
    console.debug(DEFAULT_LOG_MSG, "[TRACE]",logger + " : " + msg,Log.getFirstStackTraceFrameOpt());
  }

  public static getFirstStackTraceFrameOpt() : string {
    if(Log.checkForFirstTraceLevel()) {
      const stack = new Error().stack;

      if (stack) {
        const lines = stack.split("\n");
        // Optional: Remove the first two lines ("Error" and this log function)
        const callerLine = lines[2]?.trim(); // this is the actual caller
        const splitRes = callerLine.split("@",2).join(" ")
        return `at ${splitRes}`;
      }
    }
    return " ";
  }

  public static checkForFirstTraceLevel(): boolean {
    return (window as any).printStackTrace;
  }

  // debug/trace output is opt-in: set window.printDebugLog = true to enable
  public static checkForDebugLevel(): boolean {
    return (window as any).printDebugLog;
  }
}

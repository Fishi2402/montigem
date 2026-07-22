/* (c) https://github.com/MontiCore/monticore */

export class NotImplementedException extends Error {
  public constructor(message?: any, cause?: any, enableSuppression?: any, writableStackTrace?: any) {
    if (((typeof message === 'string') || message === null) && ((cause != null && (cause["__classes"] && cause["__classes"].indexOf("java.lang.Throwable") >= 0) || cause != null && cause instanceof <any>Error) || cause === null) && ((typeof enableSuppression === 'boolean') || enableSuppression === null) && ((typeof writableStackTrace === 'boolean') || writableStackTrace === null)) {
      let __args = arguments;
      super(message);
      this.message = message;
    } else if (((typeof message === 'string') || message === null) && ((cause != null && (cause["__classes"] && cause["__classes"].indexOf("java.lang.Throwable") >= 0) || cause != null && cause instanceof <any>Error) || cause === null) && enableSuppression === undefined && writableStackTrace === undefined) {
      let __args = arguments;
      super(message);
      this.message = message;
    } else if (((typeof message === 'string') || message === null) && cause === undefined && enableSuppression === undefined && writableStackTrace === undefined) {
      let __args = arguments;
      super(message);
      this.message = message;
    } else if (((message != null && (message["__classes"] && message["__classes"].indexOf("java.lang.Throwable") >= 0) || message != null && message instanceof <any>Error) || message === null) && cause === undefined && enableSuppression === undefined && writableStackTrace === undefined) {
      let __args = arguments;
      let cause: any = __args[0];
      super(cause);
      this.message = cause;
    } else if (message === undefined && cause === undefined && enableSuppression === undefined && writableStackTrace === undefined) {
      let __args = arguments;
      super();
    } else throw new Error('invalid overload');
  }

  /**
   * Async-constructor convention used by the JSweet transpilation
   * (AsyncConstructorAdapter): transpiled Java `new NotImplementedException(msg)`
   * becomes `await new NotImplementedException().__NotImplementedExceptionConstructor(msg)`.
   */
  public async __NotImplementedExceptionConstructor(message?: any, cause?: any, enableSuppression?: any, writableStackTrace?: any): Promise<NotImplementedException> {
    if (message !== undefined && message !== null) {
      this.message = message;
    }
    return this;
  }

  /**
   *
   * @return {string}
   */
  public getMessage(): string {
    return "The method " + this.message + " is not implemented.";
  }
}

NotImplementedException["__class"] = "umlp.backendrte.command.error.NotImplementedException";




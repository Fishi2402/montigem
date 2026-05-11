export class JavaUtilUtils {

  public static requireNonNull<T>(obj: T, message?: string): T {
    if (obj == null) {
      throw Object.defineProperty(new Error(message), '__classes', {
        configurable: true,
        value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.NullPointerException', 'java.lang.Exception']
      });
    }
    return obj;
  }

  public static async equals(a: any, b: any): Promise<boolean> {
    a = await a;
    b = await b;

    if (a === b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    if (JavaUtilUtils.isListLike(a) || JavaUtilUtils.isListLike(b)) {
      const aArray = await JavaUtilUtils.toArray(a);
      const bArray = await JavaUtilUtils.toArray(b);
      if (aArray.length !== bArray.length) {
        return false;
      }
      for (let i = 0; i < aArray.length; i++) {
        if (!(await JavaUtilUtils.equals(aArray[i], bArray[i]))) {
          return false;
        }
      }
      return true;
    }
    if (typeof a.equals === "function") {
      return await a.equals(b);
    }
    return a === b;
  }

  public static equalsSync(a: any, b: any): boolean {
    if (a === b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    if ((a instanceof Array) !== (b instanceof Array)) {
      return false;
    }
    if (a instanceof Array && b instanceof Array) {
      if (a.length !== b.length) {
        return false;
      }
      for (let i = 0; i < a.length; i++) {
        if (!JavaUtilUtils.equalsSync(a[i], b[i])) {
          return false;
        }
      }
      return true;
    }
    if (typeof a.equals === "function") {
      return a.equals(b);
    }
    return a === b;
  }

  private static isListLike(value: any): boolean {
    return value instanceof Array ||
        typeof value.toArraySync === "function" ||
        typeof value.toArray === "function";
  }

  private static async toArray(value: any): Promise<any[]> {
    value = await value;
    if (value instanceof Array) {
      return value;
    }
    if (typeof value.toArraySync === "function") {
      return await value.toArraySync();
    }
    if (typeof value.toArray === "function") {
      return await value.toArray();
    }
    return [value];
  }
}

JavaUtilUtils["__class"] = "umlp.util.jsweetUtils.JavaUtilUtils";

export class JavaUtilUtils {

  public static requireNonNull<T>(obj: T, message?: string): T {
    if (obj == null) {
      throw Object.defineProperty(new Error(message), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] });
    }
    return obj;
  }

  public static equals(a: any, b: any): boolean {
    return a === b;
  }
}
JavaUtilUtils["__class"] = "umlp.util.jsweetUtils.JavaUtilUtils";

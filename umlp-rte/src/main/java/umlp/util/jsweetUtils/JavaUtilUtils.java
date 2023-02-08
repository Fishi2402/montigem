/* (c) https://github.com/MontiCore/monticore */
package umlp.util.jsweetUtils;

import jsweet.lang.Replace;

import java.util.Objects;

public class JavaUtilUtils {
  @Replace(
                 "if (obj == null) {\n"
      + "          throw Object.defineProperty(new Error(), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] });\n"
      + "        }\n"
      + "        return obj;")
  public static <T> T requireNonNull(T obj) {
    return Objects.requireNonNull(obj);
  }

  public static <T> T requireNonNull(T obj, String message) {
    return Objects.requireNonNull(obj, message);
  }

  @Replace("return a === b;")
  public static boolean equals(Object a, Object b) {
    return Objects.equals(a, b);
  }
}

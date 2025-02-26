package umlp.cli.common;

import java.util.Objects;

public class Named<T> {
  protected final String name;
  protected final T value;


  public Named(String name, T value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Named<?> named = (Named<?>) o;
    return Objects.equals(name, named.name) && Objects.equals(value, named.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  @Override
  public String toString() {
    return "Named{" + "name='" + name + '\'' + ", value=" + value + '}';
  }

  public static String getUnnamed() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

    return "unnamed(" + stackTraceElements[3].getClassName()
        + ":" + stackTraceElements[3].getLineNumber() + "@" + stackTraceElements[3].getMethodName() + ")";
  }
}

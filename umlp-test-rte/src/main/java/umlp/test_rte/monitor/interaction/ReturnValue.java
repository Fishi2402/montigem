package umlp.test_rte.monitor.interaction;

import java.util.Objects;
import java.util.Optional;

public final class ReturnValue {
  private final Optional<Object> value;

  public ReturnValue(Optional<Object> value) {
    this.value = value;
  }

  public Optional<Object> value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ReturnValue that = (ReturnValue) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "ReturnValue{" + "value=" + value + '}';
  }
}

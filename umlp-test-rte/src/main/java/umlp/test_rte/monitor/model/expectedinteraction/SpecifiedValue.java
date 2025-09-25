package umlp.test_rte.monitor.model.expectedinteraction;

import java.util.Objects;
import java.util.Optional;

public final class SpecifiedValue {

  private final Optional<Object> value;

  public SpecifiedValue(Optional<Object> value) {
    this.value = value;
  }

  public Optional<Object> value() {
    return value;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    SpecifiedValue that = (SpecifiedValue) object;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "SpecifiedValue{" + "value=" + value + '}';
  }
}

package umlp.test_rte.monitor.model.expectedinteraction;

import java.util.Objects;
import java.util.Optional;

public final class ExpectedReturnValue {
  private Optional<SpecifiedValue> specifiedValue = Optional.empty();

  public Optional<SpecifiedValue> specifiedValue() {
    return specifiedValue;
  }

  public ExpectedReturnValue specifically(Optional<Object> value) {
    var specifiedValue = new SpecifiedValue(value);
    this.specifiedValue = Optional.of(specifiedValue);
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ExpectedReturnValue that = (ExpectedReturnValue) object;
    return Objects.equals(specifiedValue, that.specifiedValue);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(specifiedValue);
  }

  @Override
  public String toString() {
    return "ExpectedReturnValue{" + "specifiedValue=" + specifiedValue + '}';
  }

}

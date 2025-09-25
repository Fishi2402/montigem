package umlp.test_rte.monitor.model.expectedinteraction;

import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ExpectedReturn implements ExpectedInteraction {

  private final Object source;
  private final Object target;
  private Optional<ExpectedReturnValue> returnValue = Optional.empty();
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ExpectedReturn(Object source, Object target) {
    this.source = source;
    this.target = target;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(ExpectedInteractionVariantMatcher<T, E> matcher)
  throws E {
    return matcher.whenReturn(this);
  }

  public Object source() {
    return source;
  }

  public Object target() {
    return target;
  }

  public Optional<ExpectedReturnValue> returnValue() {
    return returnValue;
  }

  public ExpectedReturn withAnyValue() {
    var returnValue = new ExpectedReturnValue();
    this.returnValue = Optional.of(returnValue);
    return this;
  }

  public ExpectedReturn withValue(Optional<Object> value) {
    var returnValue = new ExpectedReturnValue().specifically(value);
    this.returnValue = Optional.of(returnValue);
    return this;
  }

  public ExpectedReturn withOCL(OCLStatement statement) {
    oclStatements.add(statement);
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ExpectedReturn that = (ExpectedReturn) object;
    return Objects.equals(source, that.source) && Objects.equals(target, that.target) &&
           Objects.equals(returnValue, that.returnValue) &&
           Objects.equals(oclStatements, that.oclStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, target, returnValue, oclStatements);
  }

  @Override
  public String toString() {
    return "ExpectedReturn{" + "source=" + source + ", target=" + target + ", returnValue=" +
           returnValue + ", oclStatements=" + oclStatements + '}';
  }
}

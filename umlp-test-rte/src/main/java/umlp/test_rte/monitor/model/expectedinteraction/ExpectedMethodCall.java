package umlp.test_rte.monitor.model.expectedinteraction;

import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ExpectedMethodCall implements ExpectedInteraction {

  private final Object source;
  private final Object target;
  private final String methodName;
  private Optional<Object[]> parameters = Optional.empty();
  private boolean opensActivityBar = false;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ExpectedMethodCall(Object source, Object target, String methodName) {
    this.source = source;
    this.target = target;
    this.methodName = methodName;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(ExpectedInteractionVariantMatcher<T, E> matcher)
  throws E {
    return matcher.whenMethodCall(this);
  }

  public Object source() {
    return source;
  }

  public Object target() {
    return target;
  }

  public String methodName() {
    return methodName;
  }

  public Optional<Object[]> parameters() {
    return parameters;
  }

  public boolean opensActivityBar() {
    return opensActivityBar;
  }

  public ExpectedMethodCall withParameters(Object... parameters) {
    this.parameters = Optional.of(parameters);
    return this;
  }

  public ExpectedMethodCall withActivityBar() {
    this.opensActivityBar = true;
    return this;
  }

  public ExpectedMethodCall withOCL(OCLStatement statement) {
    this.oclStatements.add(statement);
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ExpectedMethodCall that = (ExpectedMethodCall) object;
    return opensActivityBar == that.opensActivityBar && Objects.equals(source, that.source) &&
           Objects.equals(target, that.target) && Objects.equals(methodName, that.methodName) &&
           Objects.equals(parameters, that.parameters) &&
           Objects.equals(oclStatements, that.oclStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, target, methodName, parameters, opensActivityBar, oclStatements);
  }

  @Override
  public String toString() {
    return "ExpectedMethodCall{" + "source=" + source + ", target=" + target + ", methodName='" +
           methodName + '\'' + ", parameters=" + parameters + ", opensActivityBar=" +
           opensActivityBar + ", oclStatements=" + oclStatements + '}';
  }
}

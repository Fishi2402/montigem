package umlp.test_rte.monitor.model.expectedinteraction;

import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ExpectedConstructorCall implements ExpectedInteraction {

  private final Object source;
  private final String className;
  private final String objectName;
  private Optional<Object[]> parameters = Optional.empty();
  private boolean opensActivityBar = false;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ExpectedConstructorCall(Object source, String className, String objectName) {
    this.source = source;
    this.className = className;
    this.objectName = objectName;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(ExpectedInteractionVariantMatcher<T, E> matcher)
  throws E {
    return matcher.whenConstructorCall(this);
  }

  public Object source() {
    return source;
  }

  public String className() {
    return className;
  }

  public String objectName() {
    return objectName;
  }

  public Optional<Object[]> parameters() {
    return parameters;
  }

  public boolean opensActivityBar() {
    return opensActivityBar;
  }

  public ExpectedConstructorCall withParameters(Object... parameters) {
    this.parameters = Optional.of(parameters);
    return this;
  }

  public ExpectedConstructorCall withActivityBar() {
    this.opensActivityBar = true;
    return this;
  }

  public ExpectedConstructorCall withOCL(OCLStatement oclStatement) {
    this.oclStatements.add(oclStatement);
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ExpectedConstructorCall that = (ExpectedConstructorCall) object;
    return opensActivityBar == that.opensActivityBar && Objects.equals(source, that.source) &&
           Objects.equals(className, that.className) &&
           Objects.equals(objectName, that.objectName) &&
           Objects.equals(parameters, that.parameters) &&
           Objects.equals(oclStatements, that.oclStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, className, objectName, parameters, opensActivityBar, oclStatements);
  }

  @Override
  public String toString() {
    return "ExpectedConstructorCall{" + "source=" + source + ", className='" + className + '\'' +
           ", objectName='" + objectName + '\'' + ", parameters=" + parameters +
           ", opensActivityBar=" + opensActivityBar + ", oclStatements=" + oclStatements + '}';
  }
}

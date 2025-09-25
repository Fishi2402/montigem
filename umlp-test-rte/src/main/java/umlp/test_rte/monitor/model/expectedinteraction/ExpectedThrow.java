package umlp.test_rte.monitor.model.expectedinteraction;

import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ExpectedThrow implements ExpectedInteraction {

  private final Object source;
  private final Object target;
  private final String errorTypeName;
  private Optional<Throwable> referenceError = Optional.empty();
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ExpectedThrow(Object source, Object target, String errorTypeName) {
    this.source = source;
    this.target = target;
    this.errorTypeName = errorTypeName;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(ExpectedInteractionVariantMatcher<T, E> matcher)
  throws E {
    return matcher.whenThrow(this);
  }

  public Object source() {
    return source;
  }

  public Object target() {
    return target;
  }

  public String errorTypeName() {
    return errorTypeName;
  }

  public Optional<Throwable> referenceError() {
    return referenceError;
  }

  public ExpectedThrow withReferenceError(Throwable referenceError) {
    this.referenceError = Optional.of(referenceError);
    return this;
  }

  public ExpectedThrow withOCL(OCLStatement statement) {
    oclStatements.add(statement);
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ExpectedThrow that = (ExpectedThrow) object;
    return Objects.equals(source, that.source) && Objects.equals(target, that.target) &&
           Objects.equals(errorTypeName, that.errorTypeName) &&
           Objects.equals(referenceError, that.referenceError) &&
           Objects.equals(oclStatements, that.oclStatements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, target, errorTypeName, referenceError, oclStatements);
  }

  @Override
  public String toString() {
    return "ExpectedThrow{" + "source=" + source + ", target=" + target + ", errorTypeName='" +
           errorTypeName + '\'' + ", referenceError=" + referenceError + ", oclStatements=" +
           oclStatements + '}';
  }
}

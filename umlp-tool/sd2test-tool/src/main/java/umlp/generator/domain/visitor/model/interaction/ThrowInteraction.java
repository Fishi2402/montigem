package umlp.generator.domain.visitor.model.interaction;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ThrowInteraction implements Interaction{

  private final Actor source;
  private final Actor target;
  private final String errorClassName;
  private final Optional<List<String>> constructorParameterExpressions;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ThrowInteraction(Actor source,
      Actor target,
      String errorClassName,
      Optional<List<String>> constructorParameterExpressions) {
    this.source = source;
    this.target = target;
    this.errorClassName = errorClassName;
    this.constructorParameterExpressions = constructorParameterExpressions;
  }

  @Override
  public boolean isComplete() {
    return constructorParameterExpressions.isPresent();
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(InteractionVariantMatcher<T, E> matcher) throws E {
    return matcher.whenThrow(this);
  }

  public Actor source() {
    return source;
  }

  public Actor target() {
    return target;
  }

  public String errorClassName() {
    return errorClassName;
  }

  public Optional<List<String>> constructorParameterExpressions() {
    return constructorParameterExpressions;
  }

  @Override
  public String toString() {
    return "ThrowInteraction{" + "source=" + source + ", target=" + target + ", errorClassName='" +
           errorClassName + '\'' + ", constructorParameterExpressions=" +
           constructorParameterExpressions + ", oclStatements=" + oclStatements + '}';
  }
}

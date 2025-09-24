package umlp.generator.domain.visitor.model.interaction;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConstructorCallInteraction implements Interaction {

  private final Actor source;
  private final String className;
  private final String objectName;
  private final Optional<List<String>> parameterExpressions;
  private Optional<ActivityBar> activityBar = Optional.empty();
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ConstructorCallInteraction(Actor source,
      String className,
      String objectName,
      Optional<List<String>> parameterExpressions) {
    this.source = source;
    this.className = className;
    this.objectName = objectName;
    this.parameterExpressions = parameterExpressions;
  }

  @Override
  public boolean isComplete() {
    return parameterExpressions.isPresent();
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T> T matchVariant(InteractionVariantMatcher<T> matcher) {
    return matcher.whenConstructorCall(this);
  }

  public Actor source() {
    return source;
  }

  public String className() {
    return className;
  }

  public String objectName() {
    return objectName;
  }

  public Optional<List<String>> parameterExpressions() {
    return parameterExpressions;
  }

  public Optional<ActivityBar> activityBar() {
    return activityBar;
  }

  public ConstructorCallInteraction setActivityBar(ActivityBar activityBar) {
    this.activityBar = Optional.of(activityBar);
    return this;
  }

  @Override
  public String toString() {
    return "ConstructorCallInteraction{" + "source=" + source + ", className='" + className + '\'' +
           ", objectName='" + objectName + '\'' + ", parameterExpressions=" + parameterExpressions +
           ", activityBar=" + activityBar + ", oclStatements=" + oclStatements + '}';
  }
}

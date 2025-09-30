package umlp.generator.domain.visitor.model.interaction;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MethodCallInteraction implements Interaction {

  private final Actor source;
  private final Actor target;
  private final String methodName;
  private final boolean isStatic;
  private final boolean isTrigger;
  private final Optional<List<String>> argumentExpressions;
  private Optional<ActivityBar> activityBar = Optional.empty();
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public MethodCallInteraction(Actor source,
      Actor target,
      String methodName,
      boolean isStatic,
      boolean isTrigger,
      Optional<List<String>> argumentExpressions) {
    this.source = source;
    this.target = target;
    this.methodName = methodName;
    this.isStatic = isStatic;
    this.isTrigger = isTrigger;
    this.argumentExpressions = argumentExpressions;
  }

  @Override
  public boolean isComplete() {
    return argumentExpressions.isPresent();
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T, E extends Throwable> T matchVariant(InteractionVariantMatcher<T, E> matcher) throws E {
    return matcher.whenMethodCall(this);
  }

  public Actor source() {
    return source;
  }

  public Actor target() {
    return target;
  }

  public String methodName() {
    return methodName;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public boolean isTrigger() {
    return isTrigger;
  }

  public Optional<List<String>> argumentExpressions() {
    return argumentExpressions;
  }

  public Optional<ActivityBar> activityBar() {
    return activityBar;
  }

  public MethodCallInteraction setActivityBar(ActivityBar activityBar) {
    this.activityBar = Optional.of(activityBar);
    return this;
  }

  @Override
  public String toString() {
    return "MethodCallInteraction{" + "source=" + source + ", target=" + target + ", methodName='" +
           methodName + '\'' + ", isStatic=" + isStatic + ", isTrigger=" + isTrigger +
           ", argumentExpressions=" + argumentExpressions + ", activityBar=" + activityBar +
           ", oclStatements=" + oclStatements + '}';
  }
}

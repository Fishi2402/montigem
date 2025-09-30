package umlp.generator.domain.visitor.model.interaction;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConstructorCallInteraction implements Interaction {

  private final Actor source;
  private final String declarationTypeName;
  private final String initializationTypeName;
  private final String objectName;
  private final Optional<List<String>> parameterExpressions;
  private Optional<ActivityBar> activityBar = Optional.empty();
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ConstructorCallInteraction(Actor source,
      String declarationTypeName,
      String initializationTypeName,
      String objectName,
      Optional<List<String>> parameterExpressions) {
    this.source = source;
    this.declarationTypeName = declarationTypeName;
    this.initializationTypeName = initializationTypeName;
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
  public <T, E extends Throwable> T matchVariant(InteractionVariantMatcher<T, E> matcher) throws E {
    return matcher.whenConstructorCall(this);
  }

  public Actor source() {
    return source;
  }

  public String declarationTypeName() {
    return declarationTypeName;
  }

  public String initializationTypeName() {
    return initializationTypeName;
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
    return "ConstructorCallInteraction{" + "source=" + source + ", declarationTypeName='" +
           declarationTypeName + '\'' + ", initializationTypeName='" + initializationTypeName +
           '\'' + ", objectName='" + objectName + '\'' + ", parameterExpressions=" +
           parameterExpressions + ", activityBar=" + activityBar + ", oclStatements=" +
           oclStatements + '}';
  }
}

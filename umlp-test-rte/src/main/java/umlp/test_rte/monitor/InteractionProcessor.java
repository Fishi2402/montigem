package umlp.test_rte.monitor;

import org.junit.jupiter.api.Assertions;
import umlp.test_rte.monitor.interaction.ConstructorCallInteraction;
import umlp.test_rte.monitor.interaction.ExhaustiveStackEventVariantMatcher;
import umlp.test_rte.monitor.interaction.Interaction;
import umlp.test_rte.monitor.interaction.MethodCallInteraction;
import umlp.test_rte.monitor.interaction.ReturnInteraction;
import umlp.test_rte.monitor.interaction.StackEventVariantMatcher;
import umlp.test_rte.monitor.interaction.ThrowInteraction;
import umlp.test_rte.monitor.model.ExpectedEvent;
import umlp.test_rte.monitor.model.MatchingStrategy;
import umlp.test_rte.monitor.model.action.Action;
import umlp.test_rte.monitor.model.action.ActionVariantMatcher;
import umlp.test_rte.monitor.model.action.ActivityBarEndAction;
import umlp.test_rte.monitor.model.action.ErrorAction;
import umlp.test_rte.monitor.model.action.ExhaustiveActionVariantMatcher;
import umlp.test_rte.monitor.model.action.MethodCallAction;
import umlp.test_rte.monitor.model.action.ReturnAction;
import umlp.test_rte.monitor.model.ocl.ExhaustiveOCLStatementVariantMatcher;
import umlp.test_rte.monitor.model.ocl.OCLCondition;
import umlp.test_rte.monitor.model.ocl.OCLVariableDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Receives and processes {@link Interaction}s from the {@link MethodMonitor}
 */
public final class InteractionProcessor {

  /**
   * A stack to monitor which object is currently calling methods. If the stack is empty, calls must
   * have been caused by a trigger
   */
  private final LinkedList<Object> callstack = new LinkedList<>();
  /**
   * A map containing the matching strategies for every object declared in the test
   */
  private final Map<Object, MatchingStrategy> objectMatchingStrategies = new HashMap<>();
  /**
   * All pending {@link ExpectedEvent}s caused by an upcoming trigger
   */
  private final List<ExpectedEvent> expectedInteractions = new ArrayList<>();
  /**
   * Stack with the signatures of all active activity bars
   */
  private final LinkedList<String> activityBarSignatureStack = new LinkedList<>();
  /**
   * Whether we are currently performing a condition check and therefor ignoring incoming events
   */
  private boolean conditionChecking = false;

  /**
   * Checks whether an incoming {@link Interaction} matches the expected event flow, considering
   * their {@link #objectMatchingStrategies()}
   *
   * @param event A {@link Interaction}
   */
  public MonitorInstructions process(Interaction event) {
    if (conditionChecking) {
      return MonitorInstructions.createDefault(); //we ignore incoming stack events during a
      // condition check
    }

    var reportingObject = event.object();

    //update callstack
    var sourceTargetPair = event.matchVariant(new StackEventVariantMatcher<SourceTargetPair>() {
      @Override
      public SourceTargetPair whenMethodCall(MethodCallInteraction methodCall) {
        return pushReportingObject();
      }

      @Override
      public SourceTargetPair whenConstructorCall(ConstructorCallInteraction constructorCall) {
        return pushReportingObject();
      }

      private SourceTargetPair pushReportingObject() {
        callstack.add(reportingObject);
        return new SourceTargetPair(callstack.size() >= 2 ?
                                        Optional.of(callstack.get(callstack.size() - 2)) :
                                        Optional.empty(), Optional.of(reportingObject));
      }

      @Override
      public SourceTargetPair otherwise(Interaction interaction) {
        if (reportingObject != callstack.pollLast()) {
          throw new IllegalStateException(
              "exiting from " + reportingObject + "#" + interaction.signature() +
              " whose entry was never logged");
        }

        assert reportingObject != null : "reporting object is never null";
        return new SourceTargetPair(Optional.of(reportingObject),
                                    Optional.ofNullable(callstack.peekLast()));
      }
    });
    var sourceObject = sourceTargetPair.sourceObject;
    var targetObject = sourceTargetPair.targetObject;

    //check whether the event does match the expected event
    var errorMessage = matchesExpectedEvent(event, sourceObject, targetObject);

    if (errorMessage.isEmpty()) {
      var expectedEvent = expectedInteractions.remove(0); //remove the expected event
      var isCallWithActivityBar = expectedEvent
          .action()
          .matchVariant(new ActionVariantMatcher<Boolean>() {
            @Override
            public Boolean whenMethodCall(MethodCallAction methodCall) {
              return methodCall.hasActivityBar();
            }

            @Override
            public Boolean otherwise(Action action) {
              return false;
            }
          });

      //check whether the next expected event is the end of an activity bar and is also matched by
      // the current one
      var nextIsEndOfActivityBar = !expectedInteractions.isEmpty() && expectedInteractions
          .get(0)
          .action()
          .matchVariant(new ActionVariantMatcher<Boolean>() {
            @Override
            public Boolean whenActivityBarEnd(ActivityBarEndAction activityBarEnd) {
              return true;
            }

            @Override
            public Boolean otherwise(Action action) {
              return false;
            }
          });
      Supplier<Boolean> alsoMatchesNextEvent = () -> matchesExpectedEvent(event,
                                                                          sourceObject,
                                                                          targetObject).isEmpty();
      if (nextIsEndOfActivityBar && alsoMatchesNextEvent.get()) {
        expectedInteractions.remove(0); //also remove the expected activity bar end
        activityBarSignatureStack.removeLast(); //remove the signature
      }

      //no need to check whether the match was actually required
      return MonitorInstructions.createDefault().setReportActivityEnd(isCallWithActivityBar);
    }

    //get the highest priority matching strategy of source and target object
    var matchingStrategy = Stream
        .concat(sourceObject.stream(), targetObject.stream())
        .flatMap(object -> Optional.ofNullable(objectMatchingStrategies.get(object)).stream())
        .max(Comparator.naturalOrder())
        .orElseGet(MatchingStrategy::getDefault);

    //determine whether the event must match the expected event
    var requiredMatch = isRequiredMatch(matchingStrategy, sourceObject, targetObject, event);

    if (!requiredMatch) {
      //we just ignore non-required matches
      return MonitorInstructions.createDefault();
    }

    //raise an error
    Assertions.fail(errorMessage.get());
    return MonitorInstructions.createDefault();
  }

  private Optional<String> matchesExpectedEvent(Interaction interaction,
      Optional<Object> sourceObject,
      Optional<Object> targetObject) {
    if (expectedInteractions.isEmpty()) {
      return Optional.of("not expecting any interaction");
    }
    var expectedEvent = expectedInteractions.get(0);

    //check source-object
    //the only case for a source object to not be present is it being a trigger, and we have no
    // way to control that
    if (sourceObject.isPresent()) {
      var expectedSource = expectedEvent.source();
      var actualSource = sourceObject.get();
      if (expectedSource != actualSource) {
        return Optional.of(String.format("Expected source object %s but got %s instead",
                                         expectedSource,
                                         actualSource));
      }
    }

    //check target-object
    //the only case for a target-object to not be present is a return to a trigger, and we have
    // no way to control that
    if (targetObject.isPresent()) {
      var expectedTarget = expectedEvent.target();
      var actualTarget = targetObject.get();
      if (expectedTarget != actualTarget) {
        return Optional.of(String.format("Expected target object %s but got %s instead",
                                         expectedTarget,
                                         actualTarget));
      }
    }

    //check the action
    var actionCheckErrorMessage = expectedEvent
        .action()
        .matchVariant(new ExhaustiveActionVariantMatcher<Optional<String>>() {
          @Override
          public Optional<String> whenMethodCall(MethodCallAction call) {
            var expectedMethodName = call.methodName();
            return interaction.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenMethodCall(MethodCallInteraction methodCall) {
                //check method name
                var actualMethodName = methodCall.methodName();
                if (!actualMethodName.equals(expectedMethodName)) {
                  return Optional.of(String.format("Expected call of %s but got call of %s",
                                                   expectedMethodName,
                                                   actualMethodName));
                }

                //check arguments
                if (call.args().isPresent()) {
                  var expectedArguments = call.args().get();
                  var actualArguments = methodCall.params();
                  if (!Arrays.equals(expectedArguments, actualArguments)) {
                    return Optional.of(String.format("Expected %s(%s) but got %s(%s)",
                                                     expectedMethodName,
                                                     Arrays
                                                         .stream(expectedArguments)
                                                         .map(String::valueOf)
                                                         .collect(Collectors.joining(", ")),
                                                     actualMethodName,
                                                     Arrays
                                                         .stream(actualArguments)
                                                         .map(String::valueOf)
                                                         .collect(Collectors.joining(", "))));
                  }
                }

                return Optional.empty();
              }

              @Override
              public Optional<String> otherwise(Interaction interaction) {
                return Optional.of(String.format("Expected call of %s but got %s",
                                                 expectedMethodName,
                                                 interaction));
              }
            });
          }

          @Override
          public Optional<String> whenError(ErrorAction errorAction) {
            return interaction.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenError(ThrowInteraction throwInteraction) {
                //check error-type
                var expectedErrorType = errorAction.errorTypeName();
                var actualErrorType = throwInteraction.errorTypeName();
                if (!actualErrorType.equals(expectedErrorType)) {
                  return Optional.of(
                      "Expected error of type " + expectedErrorType + " but got error of type " +
                      actualErrorType);
                }

                //check error
                if (errorAction.error().isPresent()) {
                  var expectedError = errorAction.error().get();
                  var actualError = throwInteraction.error();
                  if (!actualError.equals(expectedError)) {
                    return Optional.of(String.format("Expected %s but got %s",
                                                     expectedError,
                                                     actualError));
                  }
                }

                return Optional.empty();
              }

              @Override
              public Optional<String> otherwise(Interaction interaction) {
                return Optional.of("Expected error but got " + interaction);
              }
            });
          }

          @Override
          public Optional<String> whenReturn(ReturnAction returnAction) {
            return interaction.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenReturn(ReturnInteraction returnInteraction) {
                var optReturnValue = returnInteraction.returnValue();
                if (optReturnValue.isPresent()) {
                  //check that return value is expected
                  var returnValue = optReturnValue.get();
                  if (!returnAction.hasReturnValue()) {
                    return Optional.of("Expected void return but got " + returnValue);
                  }

                  //check return value
                  var expectedReturnValue = returnAction.returnValue();
                  var actualReturnValue = returnValue.value();
                  if (!Objects.equals(expectedReturnValue, actualReturnValue)) {
                    return Optional.of(String.format("Expected return %s but got %s",
                                                     expectedReturnValue,
                                                     actualReturnValue));
                  }

                } else {
                  //check that void is expected
                  if (returnAction.hasReturnValue()) {
                    return Optional.of("Expected return value but got none");
                  }

                }
                return Optional.empty();
              }

              @Override
              public Optional<String> otherwise(Interaction interaction) {
                return Optional.of("Expected return but got " + interaction);
              }
            });
          }

          @Override
          public Optional<String> whenActivityBarEnd(ActivityBarEndAction activityBarEnd) {
            return interaction.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenError(ThrowInteraction throwInteraction) {
                return checkSignature("error");
              }

              @Override
              public Optional<String> whenReturn(ReturnInteraction returnInteraction) {
                return checkSignature("return");
              }

              private Optional<String> checkSignature(String exitKind) {
                var actualSignature = interaction.signature();
                var expectedSignature = activityBarSignatureStack.getLast();
                if (!actualSignature.equals(expectedSignature)) {
                  return Optional.of(
                      "Expected end of " + expectedSignature + " but got " + exitKind + " from " +
                      actualSignature);
                }
                return Optional.empty();
              }

              @Override
              public Optional<String> otherwise(Interaction interaction) {
                return Optional.of("Expected end of activity bar, but got " + interaction);
              }
            });
          }
        });

    if (actionCheckErrorMessage.isPresent()) {
      return actionCheckErrorMessage;
    }

    //execute ocl statements
    for (var oclStatement : expectedEvent.oclStatements()) {
      var statementError =
          oclStatement.matchVariant(new ExhaustiveOCLStatementVariantMatcher<Optional<String>>() {
        @Override
        public Optional<String> whenCondition(OCLCondition condition) {
          boolean conditionCheckResult;
          InteractionProcessor.this.conditionChecking = true;
          try {
            conditionCheckResult = condition.check();
          } finally {
            InteractionProcessor.this.conditionChecking = false;
          }
          if (!conditionCheckResult) {
            return Optional.of("Failed condition check: " + condition.asString());
          }
          return Optional.empty();
        }

        @Override
        public Optional<String> whenVariableDeclaration(OCLVariableDeclaration variableDeclaration) {
          variableDeclaration.execute();
          return Optional.empty();
        }
      });
      if (statementError.isPresent()) {
        return statementError;
      }
    }

    return Optional.empty();
  }

  /**
   * Determines whether the next incoming event must match the expected event based on the matching
   * strategy and some more info of the objects involved in the incoming event
   *
   * @param matchingStrategy The {@link MatchingStrategy} used
   * @param sourceObject     The object initiating the interaction, if present
   * @param targetObject     The object targeted by the interaction, if present
   * @param event            The incoming event
   * @return Whether the incoming event must match the expected event, {@code false} implying that a
   * mismatch can be ignored
   */
  private boolean isRequiredMatch(MatchingStrategy matchingStrategy,
      Optional<Object> sourceObject,
      Optional<Object> targetObject,
      Interaction event) {
    switch (matchingStrategy) {
      case FREE:
        //matching is always optional
        return false;
      case INITIAL:
        //matching is required if an expected event exists, which is of the same kind as the
        // reported event
        return expectedInteractions.stream().anyMatch(expectedEvent -> {
          //source must match
          if (sourceObject.isPresent() && sourceObject.get() != expectedEvent.source()) {
            return false;
          }
          //target must match
          if (targetObject.isPresent() && targetObject.get() != expectedEvent.target()) {
            return false;
          }
          //action must match
          return event.matchVariant(new ExhaustiveStackEventVariantMatcher<>() {
            @Override
            public Boolean whenMethodCall(MethodCallInteraction event) {
              return expectedEvent.action().matchVariant(new ActionVariantMatcher<>() {
                @Override
                public Boolean whenMethodCall(MethodCallAction methodCall) {
                  //method name must match
                  if (!event.methodName().equals(methodCall.methodName())) {
                    return false;
                  }
                  return methodCall.args().isEmpty() ||
                         Arrays.equals(methodCall.args().get(), event.params());
                }

                @Override
                public Boolean otherwise(Action action) {
                  return false;
                }
              });
            }

            @Override
            public Boolean whenConstructorCall(ConstructorCallInteraction constructorCall) {
              //TODO implement expected constructor calls
              return false;
            }

            @Override
            public Boolean whenError(ThrowInteraction throwInteraction) {
              return expectedEvent.action().matchVariant(new ActionVariantMatcher<>() {
                @Override
                public Boolean whenError(ErrorAction errorAction) {
                  if (!errorAction.errorTypeName().equals(throwInteraction.errorTypeName())) {
                    return false;
                  }
                  return errorAction.error().isEmpty() ||
                         errorAction.error().get().equals(throwInteraction.error());
                }

                @Override
                public Boolean otherwise(Action action) {
                  return false;
                }
              });
            }

            @Override
            public Boolean whenReturn(ReturnInteraction returnInteraction) {
              var optReturnValue = returnInteraction.returnValue();
              if (optReturnValue.isPresent()) {
                var returnValue = optReturnValue.get();
                return expectedEvent.action().matchVariant(new ActionVariantMatcher<>() {
                  @Override
                  public Boolean whenReturn(ReturnAction returnAction) {
                    return returnAction.hasReturnValue() &&
                           Objects.equals(returnAction.returnValue(), returnValue.value());
                  }

                  @Override
                  public Boolean otherwise(Action action) {
                    return false;
                  }
                });
              } else {
                return expectedEvent.action().matchVariant(new ActionVariantMatcher<>() {
                  @Override
                  public Boolean whenReturn(ReturnAction returnAction) {
                    return !returnAction.hasReturnValue();
                  }

                  @Override
                  public Boolean otherwise(Action action) {
                    return false;
                  }
                });
              }


            }
          });
        });
      case VISIBLE:
        //matching is required if source and target object are declared in the test
        return Stream
            .concat(sourceObject.stream(), targetObject.stream())
            .allMatch(objectMatchingStrategies::containsKey);
      case COMPLETE:
        //matching is always required
        return true;
      default:
        throw new IllegalStateException("unknown matching strategy:  " + matchingStrategy);
    }
  }

  /**
   * Tell this processor which events to expect next.
   *
   * <p>Will clear still pending events. Use {@link #assertAllExpectedEventsToBeProcessed()} to be
   * sure that no events are pending anymore.
   *
   * @param expectedEvents Which events to expect next.
   */
  public void load(ExpectedEvent... expectedEvents) {
    this.expectedInteractions.clear();
    this.expectedInteractions.addAll(List.of(expectedEvents));
  }

  /**
   * Asserts that all {@link ExpectedEvent}s have been processed by this
   */
  public void assertAllExpectedEventsToBeProcessed() {
    //TODO if we did not progress because of a condition, it would be nice to see that condition
    // in the error
    Assertions.assertTrue(expectedInteractions.isEmpty(), () -> {
      var joiner = new StringJoiner("\n");
      joiner.add("The following events did not occur:");
      expectedInteractions.forEach(event -> joiner.add(event.toString()));
      joiner.add("Printing object callstack:");
      callstack.forEach(object -> joiner.add(object.toString()));
      return joiner.toString();
    });
  }

  /**
   * Resets the object callstack and expected events.
   *
   * <p>Note: Does not reset configured object matching strategies
   */
  public void reset() {
    callstack.clear();
    expectedInteractions.clear();
  }

  /**
   * Gets a reflective map declaring the different {@link MatchingStrategy}s of participants in a
   * sequence diagram.
   *
   * @return The different matching strategies
   */
  public Map<Object, MatchingStrategy> objectMatchingStrategies() {
    return objectMatchingStrategies;
  }

  /**
   * Helper return type for source and target object calculation
   */
  private static class SourceTargetPair {

    public final Optional<Object> sourceObject;
    public final Optional<Object> targetObject;

    public SourceTargetPair(Optional<Object> sourceObject, Optional<Object> targetObject) {
      this.sourceObject = sourceObject;
      this.targetObject = targetObject;
    }
  }
}

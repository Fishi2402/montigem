package umlp.test_rte.monitor;

import org.junit.jupiter.api.Assertions;
import umlp.test_rte.monitor.interaction.ConstructorCallInteraction;
import umlp.test_rte.monitor.interaction.ExhaustiveInteractionVariantMatcher;
import umlp.test_rte.monitor.interaction.Interaction;
import umlp.test_rte.monitor.interaction.InteractionVariantMatcher;
import umlp.test_rte.monitor.interaction.MethodCallInteraction;
import umlp.test_rte.monitor.interaction.ReturnInteraction;
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
import umlp.test_rte.monitor.model.expectedinteraction.ExhaustiveExpectedInteractionVariantMatcher;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedConstructorCall;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedEndOfActivityBar;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedInteraction;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedInteractionVariantMatcher;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedMethodCall;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedReturn;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedThrow;
import umlp.test_rte.monitor.model.ocl.ExhaustiveOCLStatementVariantMatcher;
import umlp.test_rte.monitor.model.ocl.OCLCondition;
import umlp.test_rte.monitor.model.ocl.OCLVariableDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
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

  public static final String SOURCE = "source";
  public static final String TARGET = "target";
  /**
   * The default {@link MatchingStrategy} to use
   */
  private final MatchingStrategy defaultMatchingStrategy;
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
   * All pending {@link ExpectedInteraction}s caused by an upcoming trigger
   */
  private final List<ExpectedInteraction> expectedInteractions = new ArrayList<>();
  /**
   * Stack with the signatures of all active activity bars
   */
  @Deprecated private final LinkedList<String> activityBarSignatureStack = new LinkedList<>();
  /**
   * Whether we are currently performing a condition check and therefore ignoring incoming events
   */
  private boolean conditionChecking = false;
  /**
   * The message of the last encountered error. Presence indicates, that further checks can be
   * skipped.
   */
  private Optional<String> error = Optional.empty();

  public InteractionProcessor(MatchingStrategy defaultMatchingStrategy) {
    this.defaultMatchingStrategy = defaultMatchingStrategy;
  }

  /**
   * Checks whether an incoming {@link Interaction} matches the expected event flow, considering
   * their {@link #objectMatchingStrategies()}
   *
   * @param interaction A {@link Interaction}
   * @return Instructions for the {@link MethodMonitor}
   */
  public MonitorInstructions process(Interaction interaction) {
    if (error.isPresent()) {
      //ignore further interactions, until control flow returns to the test, and we can fail the
      //test
      return MonitorInstructions.createDefault();
    }

    if (conditionChecking) {
      //we ignore incoming interactions during a condition check
      return MonitorInstructions.createDefault();
    }

    var reportingObject = interaction.object();

    //update callstack
    var sourceTargetPair =
        interaction.matchVariant(new ExhaustiveInteractionVariantMatcher<SourceTargetPair,
            RuntimeException>() {
      @Override
      public SourceTargetPair whenMethodCall(MethodCallInteraction methodCall) {
        return pushReportingObject();
      }

      @Override
      public SourceTargetPair whenConstructorCall(ConstructorCallInteraction constructorCall) {
        return pushReportingObject();
      }

      @Override
      public SourceTargetPair whenError(ThrowInteraction throwInteraction) {
        return pollReportingObject();
      }

      @Override
      public SourceTargetPair whenReturn(ReturnInteraction returnInteraction) {
        return pollReportingObject();
      }

      private SourceTargetPair pushReportingObject() {
        callstack.add(reportingObject);
        return new SourceTargetPair(callstack.size() >= 2 ?
                                        Optional.of(callstack.get(callstack.size() - 2)) :
                                        Optional.empty(), Optional.of(reportingObject));
      }

      private SourceTargetPair pollReportingObject() {
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

    //check whether the interaction does match the expected interaction
    Optional<String> errorMessage;
    try {
      checkIfMatchesExpectedInteraction(interaction, sourceObject, targetObject);
      errorMessage = Optional.empty();
    } catch (UnexpectedInteractionException e) {
      errorMessage = Optional.of(e.getMessage());
    }

    if (errorMessage.isEmpty()) {
      var expectedInteraction = expectedInteractions.remove(0); //remove the expected interaction
      var isCallWithActivityBar =
          expectedInteraction.matchVariant(new ExpectedInteractionVariantMatcher<Boolean,
              RuntimeException>() {
        @Override
        public Boolean whenMethodCall(ExpectedMethodCall expectedMethodCall) {
          return expectedMethodCall.opensActivityBar();
        }

        @Override
        public Boolean whenConstructorCall(ExpectedConstructorCall expectedConstructorCall) {
          return expectedConstructorCall.opensActivityBar();
        }

        @Override
        public Boolean otherwise(ExpectedInteraction expectedInteraction) {
          return false;
        }
      });

      //check whether the next expected interaction is the end of an activity bar and is also
      // matched by
      // the current one
      var nextIsEndOfActivityBar = !expectedInteractions.isEmpty() && expectedInteractions
          .get(0)
          .matchVariant(new ExpectedInteractionVariantMatcher<Boolean, RuntimeException>() {
            @Override
            public Boolean whenEndOfActivityBar(ExpectedEndOfActivityBar expectedEndOfActivityBar) {
              return true;
            }

            @Override
            public Boolean otherwise(ExpectedInteraction expectedInteraction) {
              return false;
            }
          });
      Supplier<Boolean> alsoMatchesNextEvent = () -> matchesExpectedInteraction(interaction,
                                                                                sourceObject,
                                                                                targetObject);
      if (nextIsEndOfActivityBar && alsoMatchesNextEvent.get()) {
        expectedInteractions.remove(0); //also remove the expected activity bar end
      }

      //no need to check whether the match was actually required
      return MonitorInstructions.createDefault().setReportActivityEnd(isCallWithActivityBar);
    }

    //get the matching strategies of all objects involved
    var involvedMatchingStrategies = Stream
        .concat(sourceObject.stream(), targetObject.stream())
        .map(object -> Optional
            .ofNullable(objectMatchingStrategies.get(object))
            .orElse(defaultMatchingStrategy))
        .collect(Collectors.toList());

    //determine whether the event must match the expected event
    var requiredMatch = involvedMatchingStrategies
        .stream()
        .anyMatch(matchingStrategy -> isRequiredMatch(matchingStrategy,
                                                      sourceObject,
                                                      targetObject,
                                                      interaction));

    if (!requiredMatch) {
      //we just ignore non-required matches
      return MonitorInstructions.createDefault();
    }

    //set error bit
    /*
     * DON'T throw an error, since the monitors may catch that and report it
     * */
    this.error = errorMessage;
    return MonitorInstructions.createDefault();
  }

  private boolean matchesExpectedInteraction(Interaction interaction,
      Optional<Object> sourceObject,
      Optional<Object> targetObject) {
    try {
      checkIfMatchesExpectedInteraction(interaction, sourceObject, targetObject);
      return true;
    } catch (UnexpectedInteractionException e) {
      return false;
    }
  }

  private void checkIfMatchesExpectedInteraction(Interaction interaction,
      Optional<Object> sourceObject,
      Optional<Object> targetObject) throws UnexpectedInteractionException {
    if (expectedInteractions.isEmpty()) {
      throw new UnexpectedInteractionException("not expecting any interaction");
    }
    var expectedInteraction = expectedInteractions.get(0);

    expectedInteraction.matchVariant(new ExhaustiveExpectedInteractionVariantMatcher<Void,
        UnexpectedInteractionException>() {
      @Override
      public Void whenMethodCall(ExpectedMethodCall expectedMethodCall)
      throws UnexpectedInteractionException {
        checkActor(sourceObject, Optional.of(expectedMethodCall.source()), SOURCE);
        checkActor(targetObject, Optional.of(expectedMethodCall.target()), TARGET);

        var expectedName = expectedMethodCall.methodName();

        interaction.matchVariant(new InteractionVariantMatcher<Void,
            UnexpectedInteractionException>() {

          @Override
          public Void whenMethodCall(MethodCallInteraction methodCall)
          throws UnexpectedInteractionException {
            //check name
            var name = methodCall.methodName();
            if (!name.equals(expectedName)) {
              throw new UnexpectedInteractionException(String.format(
                  "expected call of method %s but got call of method %s instead",
                  expectedName,
                  name));
            }

            checkParameters(expectedMethodCall.parameters(), methodCall.params(), name);

            return null;
          }

          @Override
          public Void otherwise(Interaction interaction) throws UnexpectedInteractionException {
            throw new UnexpectedInteractionException(String.format(
                "expected call of method %s but got %s",
                expectedName,
                interaction));
          }
        });

        return null;
      }

      @Override
      public Void whenConstructorCall(ExpectedConstructorCall expectedConstructorCall) {
        //TODO continue implementing here
        return null;
      }

      @Override
      public Void whenReturn(ExpectedReturn expectedReturn) {
        return null;
      }

      @Override
      public Void whenThrow(ExpectedThrow expectedThrow) {
        return null;
      }

      @Override
      public Void whenEndOfActivityBar(ExpectedEndOfActivityBar expectedEndOfActivityBar) {
        return null;
      }
    });

    //check the action
    var actionCheckErrorMessage = expectedInteraction
        .action()
        .matchVariant(new ExhaustiveActionVariantMatcher<Optional<String>>() {


          @Override
          public Optional<String> whenError(ErrorAction errorAction) {
            return interaction.matchVariant(new InteractionVariantMatcher<>() {
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
            return interaction.matchVariant(new InteractionVariantMatcher<>() {
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
            return interaction.matchVariant(new InteractionVariantMatcher<>() {
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
    for (var oclStatement : expectedInteraction.oclStatements()) {
      var statementError =
          oclStatement.matchVariant(new ExhaustiveOCLStatementVariantMatcher<Optional<String>>() {
        @Override
        public Optional<String> whenCondition(OCLCondition condition) {
          boolean conditionCheckResult;
          InteractionProcessor.this.conditionChecking = true;
          try {
            conditionCheckResult = condition.evaluate();
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

  private void checkActor(Optional<Object> actor, Optional<Object> expectedActor, String kind)
  throws UnexpectedInteractionException {
    if (actor.isEmpty()) return;
    if (expectedActor.isEmpty()) return;

    var actorObject = actor.get();
    var expectedActorObject = expectedActor.get();
    if (actorObject.equals(expectedActorObject)) return;

    throw new UnexpectedInteractionException(String.format(
        "Expected %s object %s but got %s instead",
        kind,
        expectedActorObject,
        actorObject));
  }

  private void checkParameters(Optional<Object[]> expectedParameters,
      Object[] parameters,
      String callName) throws UnexpectedInteractionException {
    if (expectedParameters.isEmpty()) return;

    var expectedParameterArray = expectedParameters.get();
    if (Arrays.equals(parameters, expectedParameterArray)) return;

    var expectedParamRepr = Arrays
        .stream(expectedParameterArray)
        .map(String::valueOf)
        .collect(Collectors.joining(", "));
    var paramRepr = Arrays
        .stream(parameters)
        .map(String::valueOf)
        .collect(Collectors.joining(", "));
    throw new UnexpectedInteractionException(String.format("Expected %s(%s) but got %s(%s) instead",
                                                           callName,
                                                           expectedParamRepr,
                                                           callName,
                                                           paramRepr));
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
          return event.matchVariant(new ExhaustiveInteractionVariantMatcher<>() {
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

  private static final class UnexpectedInteractionException extends Exception {

    public UnexpectedInteractionException(String message) {
      super(message);
    }
  }
}

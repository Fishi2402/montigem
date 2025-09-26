package umlp.test_rte.monitor;

import org.junit.jupiter.api.Assertions;
import umlp.test_rte.monitor.interaction.ConstructorCallInteraction;
import umlp.test_rte.monitor.interaction.ExhaustiveInteractionVariantMatcher;
import umlp.test_rte.monitor.interaction.Interaction;
import umlp.test_rte.monitor.interaction.InteractionVariantMatcher;
import umlp.test_rte.monitor.interaction.MethodCallInteraction;
import umlp.test_rte.monitor.interaction.ReturnInteraction;
import umlp.test_rte.monitor.interaction.ReturnValue;
import umlp.test_rte.monitor.interaction.ThrowInteraction;
import umlp.test_rte.monitor.model.MatchingStrategy;
import umlp.test_rte.monitor.model.expectedinteraction.ExhaustiveExpectedInteractionVariantMatcher;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedConstructorCall;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedEndOfActivityBar;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedInteraction;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedInteractionVariantMatcher;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedMethodCall;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedReturn;
import umlp.test_rte.monitor.model.expectedinteraction.ExpectedReturnValue;
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
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

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
   * Whether we are currently processing an interaction and therefore ignoring further incoming
   * interactions
   */
  private boolean busy = false;
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
      //ignore further interactions, until control-flow returns to the test, and we can fail the
      //test
      return MonitorInstructions.createDefault();
    }

    if (busy) {
      //we ignore incoming interactions during a condition check
      return MonitorInstructions.createDefault();
    }

    this.busy = true;
    try {
      return unprotectedProcess(interaction);
    } finally {
      this.busy = false;
    }
  }

  private MonitorInstructions unprotectedProcess(Interaction interaction) {
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
          throw new IllegalStateException(format("exiting from %s#%s whose entry was never logged",
                                                 reportingObject,
                                                 interaction.signature()));
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
                                                 Optional<Object> targetObject)
  throws UnexpectedInteractionException {
    if (expectedInteractions.isEmpty()) {
      throw new UnexpectedInteractionException("not expecting any interaction");
    }
    var expectedInteraction = expectedInteractions.get(0);

    //check the interaction against the expected interaction
    expectedInteraction.matchVariant(new ExhaustiveExpectedInteractionVariantMatcher<Void,
        UnexpectedInteractionException>() {
      @Override
      public Void whenMethodCall(ExpectedMethodCall expectedMethodCall)
      throws UnexpectedInteractionException {
        checkActor(sourceObject, expectedMethodCall.source(), SOURCE);
        checkActor(targetObject, expectedMethodCall.target(), TARGET);

        var expectedName = expectedMethodCall.methodName();

        return interaction.matchVariant(new InteractionVariantMatcher<Void,
            UnexpectedInteractionException>() {

          @Override
          public Void whenMethodCall(MethodCallInteraction methodCall)
          throws UnexpectedInteractionException {
            var name = methodCall.methodName();
            checkName("call of method", expectedName, name);
            checkParameters(expectedMethodCall.parameters(), methodCall.params(), name);
            return null;
          }

          @Override
          public Void otherwise(Interaction interaction) throws UnexpectedInteractionException {
            return raiseUnexpected(format("call of method %s", expectedName), interaction);
          }
        });
      }

      @Override
      public Void whenConstructorCall(ExpectedConstructorCall expectedConstructorCall)
      throws UnexpectedInteractionException {
        checkActor(sourceObject, expectedConstructorCall.source(), SOURCE);

        var expectedName = expectedConstructorCall.className();
        return interaction.matchVariant(new InteractionVariantMatcher<Void,
            UnexpectedInteractionException>() {
          @Override
          public Void whenConstructorCall(ConstructorCallInteraction constructorCall)
          throws UnexpectedInteractionException {
            checkName("instantiation of type", expectedName, constructorCall.className());
            checkParameters(expectedConstructorCall.parameters(),
                            constructorCall.params(),
                            expectedName);

            //instantiate variables before evaluating ocl statements
            expectedConstructorCall.init(constructorCall.object());

            return null;
          }

          @Override
          public Void otherwise(Interaction interaction) throws UnexpectedInteractionException {
            return raiseUnexpected(format("call of constructor %s", expectedName), interaction);
          }
        });
      }

      @Override
      public Void whenReturn(ExpectedReturn expectedReturn) throws UnexpectedInteractionException {
        checkActor(sourceObject, expectedReturn.source(), SOURCE);
        checkActor(targetObject, expectedReturn.target(), TARGET);

        var expectedReturnValue = expectedReturn.returnValue();

        return interaction.matchVariant(new InteractionVariantMatcher<Void,
            UnexpectedInteractionException>() {

          @Override
          public Void whenReturn(ReturnInteraction returnInteraction)
          throws UnexpectedInteractionException {
            var returnValue = returnInteraction.returnValue();
            if (expectedReturnValue.isEmpty()) {
              if (returnValue.isPresent()) {
                return raiseUnexpected(expectedReturnRepr(expectedReturnValue),
                                       returnRepr(returnValue));
              }

              return null;
            }

            if (returnValue.isEmpty()) {
              return raiseUnexpected(expectedReturnRepr(expectedReturnValue),
                                     returnRepr(returnValue));
            }

            var specifiedValue = expectedReturnValue.get().specifiedValue();
            if (specifiedValue.isEmpty()) return null;

            var expectedValue = specifiedValue.get().value();
            if (expectedValue.equals(returnValue.get().value())) return null;

            raiseUnexpected(expectedReturnRepr(expectedReturnValue), returnRepr(returnValue));
            return null;
          }

          @Override
          public Void otherwise(Interaction interaction) throws UnexpectedInteractionException {
            return raiseUnexpected(expectedReturnRepr(expectedReturnValue), interaction);
          }
        });
      }

      @Override
      public Void whenThrow(ExpectedThrow expectedThrow) throws UnexpectedInteractionException {
        checkActor(sourceObject, expectedThrow.source(), SOURCE);
        checkActor(targetObject, expectedThrow.target(), TARGET);

        return interaction.matchVariant(new InteractionVariantMatcher<Void,
            UnexpectedInteractionException>() {
          @Override
          public Void whenError(ThrowInteraction throwInteraction)
          throws UnexpectedInteractionException {
            var referenceError = expectedThrow.referenceError();
            if (referenceError.isPresent()) {
              var expectedError = referenceError.get();
              var actualError = throwInteraction.error();
              if (!actualError.equals(expectedError)) {
                return raiseUnexpected(expectedThrowRepr(expectedThrow), actualError);
              }
            } else {
              var expectedErrorType = expectedThrow.errorTypeName();
              var actualErrorType = throwInteraction.errorTypeName();
              if (!actualErrorType.equals(expectedErrorType)) {
                return raiseUnexpected(expectedThrowRepr(expectedThrow), actualErrorType);
              }
            }
            return null;
          }

          @Override
          public Void otherwise(Interaction interaction) throws UnexpectedInteractionException {
            return raiseUnexpected(expectedThrowRepr(expectedThrow), interaction);
          }
        });
      }

      @Override
      public Void whenEndOfActivityBar(ExpectedEndOfActivityBar expectedEndOfActivityBar)
      throws UnexpectedInteractionException {
        return interaction.matchVariant(new InteractionVariantMatcher<Void,
            UnexpectedInteractionException>() {
          @Override
          public Void whenError(ThrowInteraction throwInteraction)
          throws UnexpectedInteractionException {
            if (throwInteraction.endsCurrentActivityBar()) return null;

            return raiseUnexpected("end of activity bar", throwInteraction);
          }

          @Override
          public Void whenReturn(ReturnInteraction returnInteraction)
          throws UnexpectedInteractionException {
            if (returnInteraction.endsCurrentActivityBar()) return null;

            return raiseUnexpected("end of activity bar", returnInteraction);
          }

          @Override
          public Void otherwise(Interaction interaction) throws UnexpectedInteractionException {
            return raiseUnexpected("end of activity bar", interaction);
          }
        });
      }
    });

    //execute ocl statements
    for (var oclStatement : expectedInteraction.oclStatements()) {
      oclStatement.matchVariant(new ExhaustiveOCLStatementVariantMatcher<Void,
          UnexpectedInteractionException>() {
        @Override
        public Void whenCondition(OCLCondition condition) throws UnexpectedInteractionException {
          if (condition.evaluate()) return null;

          throw new UnexpectedInteractionException(
              "Failed condition check: " + condition.asString());
        }

        @Override
        public Void whenVariableDeclaration(OCLVariableDeclaration variableDeclaration) {
          variableDeclaration.execute();
          return null;
        }
      });
    }
  }

  private void checkActor(Optional<Object> actor, Object expectedActor, String kind)
  throws UnexpectedInteractionException {
    if (actor.isEmpty()) return;

    var actualActor = actor.get();
    if (actualActor.equals(expectedActor)) return;

    raiseUnexpected(format("%s object %s", kind, expectedActor), actualActor);
  }

  private void checkName(String what, String expectedName, String actualName)
  throws UnexpectedInteractionException {
    if (expectedName.equals(actualName)) return;

    raiseUnexpected(format("%s with name %s", what, expectedName),
                    format("%s with name %s", what, actualName));
  }

  private void checkParameters(Optional<Object[]> expectedParameters,
                               Object[] parameters,
                               String callName) throws UnexpectedInteractionException {
    if (expectedParameters.isEmpty()) return;

    var expectedParameterArray = expectedParameters.get();
    if (Arrays.equals(parameters, expectedParameterArray)) return;

    raiseUnexpected(callRepr(callName, expectedParameters),
                    callRepr(callName, Optional.of(parameters)));
  }

  private String callRepr(String name, Optional<Object[]> parameters) {
    var paramRepr = parameters
        .map(it -> Arrays.stream(it).map(String::valueOf).collect(Collectors.joining(", ")))
        .orElse("...");
    return String.format("%s(%s)", name, paramRepr);
  }

  private <T> T raiseUnexpected(Object expected, Object actual)
  throws UnexpectedInteractionException {
    throw new UnexpectedInteractionException(format("Expected %s but got %s instead",
                                                    expected,
                                                    actual));
  }

  private String expectedReturnRepr(Optional<ExpectedReturnValue> expectedReturnValue) {
    var valueRepr = expectedReturnValue.map(it -> it
        .specifiedValue()
        .map(specifiedValue -> specifiedValue.value().map(Object::toString).orElse("null"))
        .orElse("..."));
    return Stream.concat(Stream.of("return"), valueRepr.stream()).collect(Collectors.joining(" "));
  }

  private String returnRepr(Optional<ReturnValue> returnValue) {
    var valueRepr = returnValue.map(it -> it.value().map(Object::toString).orElse("null"));
    return Stream.concat(Stream.of("return"), valueRepr.stream()).collect(Collectors.joining(" "));
  }

  private String expectedThrowRepr(ExpectedThrow expectedThrow) {
    return "throw " + expectedThrow
        .referenceError()
        .map(String::valueOf)
        .orElse(callRepr(expectedThrow.errorTypeName(), Optional.empty()));
  }

  /**
   * Determines whether the next incoming event must match the expected event based on the matching
   * strategy and some more info of the objects involved in the incoming event
   *
   * @param matchingStrategy The {@link MatchingStrategy} used
   * @param sourceObject     The object initiating the interaction, if present
   * @param targetObject     The object targeted by the interaction, if present
   * @param interaction      The occurring interaction
   * @return Whether the incoming event must match the expected event, {@code false} implying that a
   * mismatch can be ignored
   */
  private boolean isRequiredMatch(MatchingStrategy matchingStrategy,
                                  Optional<Object> sourceObject,
                                  Optional<Object> targetObject,
                                  Interaction interaction) {
    switch (matchingStrategy) {
      case FREE:
        //matching is always optional
        return false;
      case INITIAL:
        //matching is required if an expected interaction exists, which is of the same kind as the
        // reported interaction
        return expectedInteractions
            .stream()
            .anyMatch(expectedInteraction -> interaction.matchVariant(new ExhaustiveInteractionVariantMatcher<>() {
              @Override
              public Boolean whenMethodCall(MethodCallInteraction methodCall)
              throws RuntimeException {
                return expectedInteraction.matchVariant(new ExpectedInteractionVariantMatcher<>() {
                  @Override
                  public Boolean whenMethodCall(ExpectedMethodCall expectedMethodCall)
                  throws RuntimeException {
                    //source
                    if (sourceObject.isPresent() &&
                        sourceObject.get() != expectedMethodCall.source()) {
                      return false;
                    }
                    //target
                    if (targetObject.isPresent() &&
                        targetObject.get() != expectedMethodCall.target()) {
                      return false;
                    }
                    //method name
                    if (!expectedMethodCall.methodName().equals(methodCall.methodName())) {
                      return false;
                    }
                    //parameters
                    return expectedMethodCall.parameters().isEmpty() || Arrays.equals(
                        expectedMethodCall.parameters().get(),
                        methodCall.params());
                  }

                  @Override
                  public Boolean otherwise(ExpectedInteraction expectedInteraction)
                  throws RuntimeException {
                    return false;
                  }
                });
              }

              @Override
              public Boolean whenConstructorCall(ConstructorCallInteraction constructorCall)
              throws RuntimeException {
                return expectedInteraction.matchVariant(new ExpectedInteractionVariantMatcher<>() {

                  @Override
                  public Boolean whenConstructorCall(ExpectedConstructorCall expectedConstructorCall)
                  throws RuntimeException {
                    //source
                    if (sourceObject.isPresent() &&
                        sourceObject.get() != expectedConstructorCall.source()) {
                      return false;
                    }
                    //class name
                    if (!expectedConstructorCall.className().equals(constructorCall.className())) {
                      return false;
                    }

                    //parameters
                    if (expectedConstructorCall.parameters().isEmpty()) return true;
                    return Arrays.equals(expectedConstructorCall.parameters().get(),
                                         constructorCall.params());
                  }

                  @Override
                  public Boolean otherwise(ExpectedInteraction expectedInteraction)
                  throws RuntimeException {
                    return false;
                  }
                });
              }

              @Override
              public Boolean whenError(ThrowInteraction throwInteraction) throws RuntimeException {
                return expectedInteraction.matchVariant(new ExpectedInteractionVariantMatcher<>() {

                  @Override
                  public Boolean whenThrow(ExpectedThrow expectedThrow) throws RuntimeException {
                    //source
                    if (sourceObject.isPresent() && sourceObject.get() != expectedThrow.source()) {
                      return false;
                    }
                    //target
                    if (targetObject.isPresent() && targetObject.get() != expectedThrow.target()) {
                      return false;
                    }
                    //reference error or error type name
                    return expectedThrow
                        .referenceError()
                        .map(it -> it.equals(throwInteraction.error()))
                        .orElse(expectedThrow
                                    .errorTypeName()
                                    .equals(throwInteraction.errorTypeName()));
                  }

                  @Override
                  public Boolean otherwise(ExpectedInteraction expectedInteraction)
                  throws RuntimeException {
                    return false;
                  }
                });
              }

              @Override
              public Boolean whenReturn(ReturnInteraction returnInteraction)
              throws RuntimeException {
                return expectedInteraction.matchVariant(new ExpectedInteractionVariantMatcher<>() {
                  @Override
                  public Boolean whenReturn(ExpectedReturn expectedReturn) throws RuntimeException {
                    //source
                    if (sourceObject.isPresent() && sourceObject.get() != expectedReturn.source()) {
                      return false;
                    }
                    //target
                    if (targetObject.isPresent() && targetObject.get() != expectedReturn.target()) {
                      return false;
                    }
                    //return value
                    var expectedReturnValue = expectedReturn.returnValue();
                    var returnValue = returnInteraction.returnValue();
                    if (expectedReturnValue.isPresent()) {
                      if (returnValue.isEmpty()) return false;

                      var specifiedValue = expectedReturnValue.get().specifiedValue();
                      if (specifiedValue.isEmpty()) return true;

                      var expectedValue = specifiedValue.get().value();
                      return expectedValue.equals(returnValue.get().value());
                    } else {
                      return returnValue.isEmpty();
                    }
                  }

                  @Override
                  public Boolean otherwise(ExpectedInteraction expectedInteraction)
                  throws RuntimeException {
                    return false;
                  }
                });
              }
            }));
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

  public ExpectedMethodCall expectMethodCall(Object from, Object to, String methodName) {
    var interaction = new ExpectedMethodCall(from, to, methodName);
    expectedInteractions.add(interaction);
    return interaction;
  }

  public <T> ExpectedConstructorCall expectConstructorCall(Object from,
                                                           String className,
                                                           Consumer<T> onInit) {
    var interaction = new ExpectedConstructorCall(from, className, o -> {
      @SuppressWarnings("unchecked") //we can assume that this will work, since it is mostly called
      // from generated code
      var t = (T) o;
      onInit.accept(t);
    });
    expectedInteractions.add(interaction);
    return interaction;
  }

  public ExpectedReturn expectReturn(Object from, Object to) {
    var interaction = new ExpectedReturn(from, to);
    expectedInteractions.add(interaction);
    return interaction;
  }

  public ExpectedThrow expectThrow(Object from, Object to, String errorTypeName) {
    var interaction = new ExpectedThrow(from, to, errorTypeName);
    expectedInteractions.add(interaction);
    return interaction;
  }

  public ExpectedEndOfActivityBar expectActivityEnd() {
    var interaction = new ExpectedEndOfActivityBar();
    expectedInteractions.add(interaction);
    return interaction;
  }

  /**
   * Asserts that all {@link ExpectedInteraction}s have been processed by this and that no
   * errors did occur.
   */
  public void assertExpectedInteractionsHaveOccurred() {
    Assertions.assertTrue(error.isEmpty(), () -> "An error occurred: " + error.get());
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
   * Resets the object callstack and expected interactions.
   *
   * <p>Note: Does not reset configured object matching strategies
   */
  public void reset() {
    callstack.clear();
    expectedInteractions.clear();
  }

  public void declare(Object actor, MatchingStrategy matchingStrategy) {
    objectMatchingStrategies.put(actor, matchingStrategy);
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

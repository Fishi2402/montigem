package umlp.test_rte.monitor;

import org.junit.jupiter.api.Assertions;
import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.ErrorEvent;
import umlp.test_rte.monitor.event.ReturnEvent;
import umlp.test_rte.monitor.event.ReturnVoidEvent;
import umlp.test_rte.monitor.event.StackEvent;
import umlp.test_rte.monitor.event.StackEventVariantMatcher;
import umlp.test_rte.monitor.model.ExpectedEvent;
import umlp.test_rte.monitor.model.MatchingStrategy;
import umlp.test_rte.monitor.model.action.Action;
import umlp.test_rte.monitor.model.action.ActionVariantMatcher;
import umlp.test_rte.monitor.model.action.ActivityBarEnd;
import umlp.test_rte.monitor.model.action.Call;
import umlp.test_rte.monitor.model.action.Error;
import umlp.test_rte.monitor.model.action.ExhaustiveActionVariantMatcher;
import umlp.test_rte.monitor.model.action.Return;

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
 * Receives and processes {@link StackEvent}s from the {@link MethodMonitor}
 */
public final class StackEventProcessor {

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
  private final List<ExpectedEvent> expectedEvents = new ArrayList<>();
  /**
   * Whether we are currently performing a condition check and therefor ignoring incoming events
   */
  private boolean conditionChecking = false;

  /**
   * Checks whether an incoming {@link StackEvent} matches the expected event flow, considering
   * their {@link #objectMatchingStrategies()}
   *
   * @param event A {@link StackEvent}
   */
  public void process(StackEvent event) {
    if (conditionChecking) {
      return; //we ignore incoming stack events during a condition check
    }

    var reportingObject = event.object();

    //update callstack
    var sourceTargetPair = event.matchVariant(new StackEventVariantMatcher<SourceTargetPair>() {
      @Override
      public SourceTargetPair whenCallEvent(CallEvent event) {
        callstack.add(reportingObject);
        return new SourceTargetPair(callstack.size() >= 2 ?
                                        Optional.of(callstack.get(callstack.size() - 2)) :
                                        Optional.empty(), Optional.of(reportingObject));
      }

      @Override
      public SourceTargetPair otherwise(StackEvent event) {
        if (reportingObject != callstack.pollLast()) {
          throw new IllegalStateException(
              "exiting from method " + event.methodName() + " whose entry was never logged");
        }

        //the java compiler otherwise assumes that reportingObject might be null
        assert reportingObject != null;
        return new SourceTargetPair(Optional.of(reportingObject),
                                    Optional.ofNullable(callstack.peekLast()));
      }
    });
    var sourceObject = sourceTargetPair.sourceObject;
    var targetObject = sourceTargetPair.targetObject;

    //check whether the event does match the expected event
    var errorMessage = matchesExpectedEvent(event, sourceObject, targetObject);

    if (errorMessage.isEmpty()) {
      expectedEvents.remove(0); //remove the expected event

      //check whether the next expected event is the end of an activity bar and is also matched by
      // the current one
      var nextIsEndOfActivityBar = !expectedEvents.isEmpty() && expectedEvents
          .get(0)
          .action()
          .matchVariant(new ActionVariantMatcher<Boolean>() {
            @Override
            public Boolean whenActivityBarEnd(ActivityBarEnd activityBarEnd) {
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
        expectedEvents.remove(0); //also remove the expected activity bar end
      }

      return; //no need to check whether the match was actually required
    }

    //get the highest priority matching strategy of source and target object
    var matchingStrategy = Stream
        .concat(sourceObject.stream(), targetObject.stream())
        .flatMap(object -> Optional.ofNullable(objectMatchingStrategies.get(object)).stream())
        .max(Comparator.naturalOrder())
        .orElseGet(MatchingStrategy::getDefault);

    //determine whether the event must match the expected event
    var requiredMatch = isRequiredMatch(matchingStrategy,
                                        sourceObject,
                                        targetObject,
                                        event.methodName());

    if (!requiredMatch) {
      //we just ignore non-required matches
      return;
    }

    //raise an error
    Assertions.fail(errorMessage.get());
  }

  private Optional<String> matchesExpectedEvent(StackEvent event,
      Optional<Object> sourceObject,
      Optional<Object> targetObject) {
    if (expectedEvents.isEmpty()) {
      return Optional.of("not expecting any event");
    }
    var expectedEvent = expectedEvents.get(0);

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
          public Optional<String> whenCall(Call call) {
            var expectedMethodName = call.methodName();
            return event.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenCallEvent(CallEvent event) {
                //check method name
                var actualMethodName = event.methodName();
                if (!actualMethodName.equals(expectedMethodName)) {
                  return Optional.of(String.format("Expected call of %s but got call of %s",
                                                   expectedMethodName,
                                                   actualMethodName));
                }

                //check arguments
                if (call.args().isPresent()) {
                  var expectedArguments = call.args().get();
                  var actualArguments = event.params();
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
              public Optional<String> otherwise(StackEvent event) {
                return Optional.of(String.format("Expected call of %s but got %s",
                                                 expectedMethodName,
                                                 event));
              }
            });
          }

          @Override
          public Optional<String> whenError(Error error) {
            return event.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenErrorEvent(ErrorEvent event) {
                //check error
                var expectedError = error.error();
                var actualError = event.exception();
                if (!actualError.equals(expectedError)) {
                  return Optional.of(String.format("Expected %s but got %s",
                                                   expectedError,
                                                   actualError));
                }

                return checkMethodNameMatch(expectedEvent, event);
              }

              @Override
              public Optional<String> otherwise(StackEvent event) {
                return Optional.of("Expected error but got " + event);
              }
            });
          }

          @Override
          public Optional<String> whenReturn(Return returnAction) {
            return event.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenReturnEvent(ReturnEvent event) {
                //check that return value is expected
                if (!returnAction.hasReturnValue()) {
                  return Optional.of("Expected void return but got " + event.returnValue());
                }

                //check return value
                var expectedReturnValue = returnAction.returnValue().orElse(null);
                var actualReturnValue = event.returnValue();
                if (!Objects.equals(expectedReturnValue, actualReturnValue)) {
                  return Optional.of(String.format("Expected return %s but got %s",
                                                   expectedReturnValue,
                                                   actualReturnValue));
                }

                return checkMethodNameMatch(expectedEvent, event);
              }

              @Override
              public Optional<String> whenReturnVoidEvent(ReturnVoidEvent event) {
                //check that void is expected
                if (returnAction.hasReturnValue()) {
                  return Optional.of("Expected return value but got none");
                }

                return checkMethodNameMatch(expectedEvent, event);
              }

              @Override
              public Optional<String> otherwise(StackEvent event) {
                return Optional.of("Expected return but got " + event);
              }
            });
          }

          @Override
          public Optional<String> whenActivityBarEnd(ActivityBarEnd activityBarEnd) {
            return event.matchVariant(new StackEventVariantMatcher<>() {
              @Override
              public Optional<String> whenErrorEvent(ErrorEvent event) {
                return checkMethodNameMatch(expectedEvent, event);
              }

              @Override
              public Optional<String> whenReturnEvent(ReturnEvent event) {
                return checkMethodNameMatch(expectedEvent, event);
              }

              @Override
              public Optional<String> whenReturnVoidEvent(ReturnVoidEvent event) {
                return checkMethodNameMatch(expectedEvent, event);
              }

              @Override
              public Optional<String> otherwise(StackEvent event) {
                return Optional.of("Expected end of activity bar, but got " + event);
              }
            });
          }
        });

    if (actionCheckErrorMessage.isPresent()) {
      return actionCheckErrorMessage;
    }

    //check condition
    Boolean conditionCheckOkay;
    try {
      this.conditionChecking = true;
      conditionCheckOkay = expectedEvent.condition().get();
    } finally {
      this.conditionChecking = false;
    }

    if (!conditionCheckOkay) {
      return Optional.of(
          "Failed condition check: " + expectedEvent.conditionStringRepresentation());
    }

    return Optional.empty();
  }

  private Optional<String> checkMethodNameMatch(ExpectedEvent expectedEvent, StackEvent event) {
    if (expectedEvent.getMethodName().isPresent()) {
      var expectedMethodName = expectedEvent.getMethodName().get();
      var actualMethodName = event.methodName();
      if (!actualMethodName.equals(expectedMethodName)) {
        var expectedActionKind = expectedEvent
            .action()
            .matchVariant(new ExhaustiveActionVariantMatcher<String>() {
              @Override
              public String whenCall(Call call) {
                return "call";
              }

              @Override
              public String whenError(Error error) {
                return "error";
              }

              @Override
              public String whenReturn(Return returnAction) {
                return "return";
              }

              @Override
              public String whenActivityBarEnd(ActivityBarEnd activityBarEnd) {
                return "end of activity bar";
              }
            });
        return Optional.of(String.format("Expected %s from %s but reported %s is from %s",
                                         expectedActionKind,
                                         expectedMethodName,
                                         expectedActionKind,
                                         actualMethodName));
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
   * @param methodName       The name of the method involved in the interaction, if known
   * @return Whether the incoming event must match the expected event, {@code false} implying that a
   * mismatch can be ignored
   */
  private boolean isRequiredMatch(MatchingStrategy matchingStrategy,
      Optional<Object> sourceObject,
      Optional<Object> targetObject,
      String methodName) {
    switch (matchingStrategy) {
      case FREE:
        //matching is always optional
        return false;
      case INITIAL:
        //matching is required if a matching expected event is present
        return expectedEvents.stream().anyMatch(expectedEvent -> {
          //source, target and method name must not create conflicts to match
          if (sourceObject.isPresent() && sourceObject.get() != expectedEvent.source()) {
            return false;
          }
          if (targetObject.isPresent() && targetObject.get() != expectedEvent.target()) {
            return false;
          }
          var expectedMethodName = expectedEvent.getMethodName();
          return expectedMethodName.isEmpty() || expectedMethodName.get().equals(methodName);
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
    this.expectedEvents.clear();
    this.expectedEvents.addAll(List.of(expectedEvents));
  }

  /**
   * Asserts that all {@link ExpectedEvent}s have been processed by this
   */
  public void assertAllExpectedEventsToBeProcessed() {
    Assertions.assertTrue(expectedEvents.isEmpty(), () -> {
      var joiner = new StringJoiner("\n");
      joiner.add("The following events did not occur:");
      expectedEvents.forEach(event -> joiner.add(event.toString()));
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
    expectedEvents.clear();
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

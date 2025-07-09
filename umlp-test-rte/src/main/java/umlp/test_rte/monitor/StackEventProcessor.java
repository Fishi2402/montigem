package umlp.test_rte.monitor;

import org.junit.jupiter.api.Assertions;
import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.StackEvent;
import umlp.test_rte.monitor.event.StackEventVariantMatcher;
import umlp.test_rte.monitor.model.ExpectedEvent;
import umlp.test_rte.monitor.model.MatchingStrategy;
import umlp.test_rte.monitor.model.action.Call;
import umlp.test_rte.monitor.model.action.Error;
import umlp.test_rte.monitor.model.action.ExhaustiveActionVariantMatcher;
import umlp.test_rte.monitor.model.action.Return;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
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

    //TODO is only the target matching strategy relevant? Or do we need the most restrictive one of
    // target and source?
    // How is the matching strategy defined when exiting a method?
    var reportingObject = event.object();
    var matchingStrategy = Optional.ofNullable(objectMatchingStrategies.get(reportingObject))
        .orElseGet(MatchingStrategy::getDefault);

    //update callstack
    var sourceTargetPair = event.matchVariant(new StackEventVariantMatcher<SourceTargetPair>() {
      @Override
      public SourceTargetPair whenCallEvent(CallEvent event) {
        callstack.push(reportingObject);
        return new SourceTargetPair(callstack.size() >= 2 ?
            Optional.of(callstack.get(callstack.size() - 2)) :
            Optional.empty(), Optional.of(reportingObject));
      }

      @Override
      public SourceTargetPair otherwise(StackEvent event) {
        if (reportingObject != callstack.pollLast()) {
          throw new IllegalStateException("exiting from method " + event.methodName() + " whose entry was never logged");
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
      return; //no need to check whether the match was actually required
    }

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
    return expectedEvent.action().matchVariant(new ExhaustiveActionVariantMatcher<>() {
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
            var expectedArguments = call.args();
            var actualArguments = event.params();
            if (!Arrays.equals(expectedArguments, actualArguments)) {
              return Optional.of(String.format("Expected %s(%s) but got %s(%s)",
                  expectedMethodName,
                  Arrays.stream(expectedArguments)
                      .map(String::valueOf)
                      .collect(Collectors.joining(", ")),
                  actualMethodName,
                  Arrays.stream(actualArguments)
                      .map(String::valueOf)
                      .collect(Collectors.joining(", "))));
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
        //TODO
        return Optional.empty();
      }

      @Override
      public Optional<String> whenReturn(Return returnAction) {
        //TODO
        return Optional.empty();
      }
    });
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
        return Stream.concat(sourceObject.stream(), targetObject.stream())
            .allMatch(objectMatchingStrategies::containsKey);
      case COMPLETE:
        //matching is always required
        return true;
      default:
        throw new IllegalStateException("unreachable");
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

  private static class SourceTargetPair {

    public final Optional<Object> sourceObject;
    public final Optional<Object> targetObject;

    public SourceTargetPair(Optional<Object> sourceObject, Optional<Object> targetObject) {
      this.sourceObject = sourceObject;
      this.targetObject = targetObject;
    }
  }
}

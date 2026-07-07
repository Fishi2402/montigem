package util;

import associations.test_infrastructure.AssociationsTestSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public interface SyncProtocolTestSupport extends AssociationsTestSetup {
  @BeforeEach
  default void configLog() {
    Log.enableFailQuick(false);
    Log.getFindings().clear();
  }

  @AfterEach
  default void checkLogForErrors() {
    assertEquals(0, Log.getErrorCount(),
        "Log errors:\n" + Log.getFindings().stream().map(Object::toString)
            .collect(Collectors.joining("\n")));
  }

  default void runCase(
      BuildState buildState,
      ContextPair contextPair,
      Operation operation,
      LoadState loadState) {
    activateSource(contextPair);
    SyncProtocolTestScenario scenario = createInitial(buildState, initialRelationCount(operation));
    preloadExpectedContext(contextPair, loadState, scenario);
    activateSource(contextPair);
    applyOperation(operation, scenario);
    activateExpected(contextPair);
    assertExpected(operation, scenario);
    assertDatabaseExpected(operation, scenario);
  }

  default void activateSource(ContextPair contextPair) {
    switch (contextPair) {
      case SERVER_TO_SERVER, SERVER_TO_CLIENT -> getBackend().activate();
      case CLIENT_TO_SERVER, CLIENT_TO_CLIENT -> getClients(0).activate();
    }
  }

  default void activateExpected(ContextPair contextPair) {
    switch (contextPair) {
      case SERVER_TO_SERVER, CLIENT_TO_SERVER -> getBackend().activate();
      case SERVER_TO_CLIENT, CLIENT_TO_CLIENT -> getClients(1).activate();
    }
  }

  default int initialRelationCount(Operation operation) {
    return switch (operation) {
      case BUILD_OBJECT, REPLACE -> 1;
      case ADD_AT_BEGINNING_OR_SET_OPTIONAL ->
          targetIsList() ? (targetIsSome() ? 1 : 0) : (targetIsOptional() ? 0 : 1);
      case ADD_AT_MIDDLE, ADD_AT_END -> requireTargetList(operation, 2);
      case REMOVE_AT_BEGINNING_OR_CLEAR_OPTIONAL -> targetIsList() ? (targetIsSome() ? 2 : 1) : 1;
      case REMOVE_AT_MIDDLE, REMOVE_AT_END -> requireTargetList(operation, 3);
      case DELETE_OBJECT -> targetIsList() ? (targetIsSome() ? 2 : 1) : 1;
    };
  }

  private int requireTargetList(Operation operation, int count) {
    if (!targetIsList()) {
      throw new IllegalArgumentException("Unsupported operation for single-valued association: " + operation);
    }
    return count;
  }

  default SyncProtocolTestScenario createInitial(BuildState buildState, int rhsCount) {
    return switch (buildState) {
      case DONE_TO_DONE -> createDoneToDoneInitial(rhsCount);
      case BUILDER_TO_DONE -> createBuilderToDoneInitial(rhsCount);
      case BUILDER_TO_BUILDER -> createBuilderToBuilderInitial(rhsCount);
    };
  }

  default void preloadExpectedContext(
      ContextPair contextPair,
      LoadState loadState,
      SyncProtocolTestScenario scenario) {
    if (loadState == LoadState.FULL_OBJECT_NOT_LOADED) {
      return; // TODO: unload objects in server!
    }
    activateExpected(contextPair);
    preloadLhs(scenario.lhsId);
    preloadRhs(scenario.rhs0Id);
    preloadRhs(scenario.rhs1Id);
    preloadRhs(scenario.rhs2Id);
  }

  default void assertExpected(Operation operation, SyncProtocolTestScenario scenario) {
    switch (operation) {
      case BUILD_OBJECT, ADD_AT_BEGINNING_OR_SET_OPTIONAL, ADD_AT_MIDDLE, ADD_AT_END ->
          assertRelationPresent(scenario.lhsId, scenario.targetRhsId);
      case REMOVE_AT_BEGINNING_OR_CLEAR_OPTIONAL, REMOVE_AT_MIDDLE, REMOVE_AT_END ->
          assertRelationAbsent(scenario.lhsId, scenario.targetRhsId);
      case REPLACE -> {
        assertRelationPresent(scenario.lhsId, scenario.targetRhsId);
        assertSpecificRelationAbsent(scenario.lhsId, scenario.oldRhsId);
      }
      case DELETE_OBJECT -> assertFalse(isRhsKnown(scenario.targetRhsId));
    }
  }

  default void assertDatabaseExpected(Operation operation, SyncProtocolTestScenario scenario) {
    if (!hasDatabase()) {
      return;
    }
    getBackend().activate();
    switch (operation) {
      case BUILD_OBJECT, ADD_AT_BEGINNING_OR_SET_OPTIONAL, ADD_AT_MIDDLE, ADD_AT_END ->
          assertPersistedRelationPresent(scenario.lhsId, scenario.targetRhsId);
      case REMOVE_AT_BEGINNING_OR_CLEAR_OPTIONAL, REMOVE_AT_MIDDLE, REMOVE_AT_END ->
          assertPersistedRelationAbsent(scenario.lhsId, scenario.targetRhsId);
      case REPLACE -> {
        assertPersistedRelationPresent(scenario.lhsId, scenario.targetRhsId);
        assertPersistedSpecificRelationAbsent(scenario.lhsId, scenario.oldRhsId);
      }
      case DELETE_OBJECT -> {
        assertPersistedLhsDoesNotReferenceRhs(scenario.lhsId, scenario.targetRhsId);
        assertPersistedRhsDeleted(scenario.targetRhsId);
      }
    }
  }

  boolean targetIsList();

  boolean targetIsOptional();

  boolean targetIsSome();

  boolean requiresBuilderForInitialLhs();

  SyncProtocolTestScenario createDoneToDoneInitial(int rhsCount);

  SyncProtocolTestScenario createBuilderToDoneInitial(int rhsCount);

  SyncProtocolTestScenario createBuilderToBuilderInitial(int rhsCount);

  void applyOperation(Operation operation, SyncProtocolTestScenario scenario);

  void preloadLhs(long lhsId);

  void preloadRhs(long rhsId);

  void assertRelationPresent(long lhsId, long rhsId);

  void assertRelationAbsent(long lhsId, long rhsId);

  void assertSpecificRelationAbsent(long lhsId, long rhsId);

  boolean isRhsKnown(long rhsId);

  void assertPersistedRelationPresent(long lhsId, long rhsId);

  void assertPersistedRelationAbsent(long lhsId, long rhsId);

  void assertPersistedSpecificRelationAbsent(long lhsId, long rhsId);

  void assertPersistedLhsDoesNotReferenceRhs(long lhsId, long rhsId);

  void assertPersistedRhsDeleted(long rhsId);
}

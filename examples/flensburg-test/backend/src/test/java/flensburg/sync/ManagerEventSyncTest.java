package flensburg.sync;

import hardy.flensburg.FlensburgManager;
import hardy.flensburg.FlensburgObserver;
import hardy.flensburg.Human;
import hardy.flensburg.commands.Human_subscribeManagerEvents;
import hardy.flensburg.test_infrastructure.FlensburgMockTestSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.common.NotificationScope;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagerEventSyncTest extends FlensburgMockTestSetup {
  public ManagerEventSyncTest() {
    super(2);
  }

  @Test
  public void testManagerEventsAreNotSyncedWithoutSubscription(){
    List<Long> humanAddedGemIds = new ArrayList<>();
    List<Long> humanDeletedGemIds = new ArrayList<>();

    getClients(0).activate();
    FlensburgManager.addObserver(new FlensburgObserver() {
      @Override
      public void notifyHumanAdded(Human human) {
        humanAddedGemIds.add(human.getGemId());
      }

      @Override
      public void notifyHumanDeleted(Long gemId) {
        humanDeletedGemIds.add(gemId);
      }
    });

    getBackend().activate();

    var h = FlensburgManager.humanBuilder().age(20).build().get();
    assertTrue(humanAddedGemIds.isEmpty());

    FlensburgManager.deleteHuman(h.getGemId());
    assertTrue(humanDeletedGemIds.isEmpty());
  }

  @Test
  public void testManagerEventsAreSyncedWithSubscription(){
    List<Long> humanAddedGemIds = new ArrayList<>();
    List<Long> humanDeletedGemIds = new ArrayList<>();

    getClients(0).activate();
    FlensburgManager.addObserver(new FlensburgObserver() {
      @Override
      public void notifyHumanAdded(Human human) {
        humanAddedGemIds.add(human.getGemId());
      }

      @Override
      public void notifyHumanDeleted(Long gemId) {
        humanDeletedGemIds.add(gemId);
      }

      @Override
      public NotificationScope getScope() {
        return NotificationScope.LOCAL;
      }
    });

    CommandManager.executeCommand(new Human_subscribeManagerEvents());

    getBackend().activate();

    var h = FlensburgManager.humanBuilder().age(20).build().get();
    assertEquals(List.of(h.getGemId()), humanAddedGemIds);
    FlensburgManager.deleteHuman(h.getGemId());
    assertEquals(List.of(h.getGemId()), humanDeletedGemIds);

  }

  @Disabled("See https://git.rwth-aachen.de/monticore/monticore/-/issues/3550")
  @Test
  public void testManagerEventsAreSyncedWithSubscriptionBetweenClients(){
    List<Long> humanAddedGemIds = new ArrayList<>();
    List<Long> humanDeletedGemIds = new ArrayList<>();

    getClients(0).activate();
    FlensburgManager.addObserver(new FlensburgObserver() {
      @Override
      public void notifyHumanAdded(Human human) {
        humanAddedGemIds.add(human.getGemId());
      }

      @Override
      public void notifyHumanDeleted(Long gemId) {
        humanDeletedGemIds.add(gemId);
      }
    });

    CommandManager.executeCommand(new Human_subscribeManagerEvents());

    getClients(1).activate();

    var h = FlensburgManager.humanBuilder().age(20).build().get();
    assertEquals(List.of(h.getGemId()), humanAddedGemIds);
    FlensburgManager.deleteHuman(h.getGemId());
    assertEquals(List.of(h.getGemId()), humanDeletedGemIds);
  }
}

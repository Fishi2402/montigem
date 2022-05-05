package hardy;

// HWC-T

import org.junit.Test;

import static org.junit.Assert.*;

// Tests the frontend HWC PersonPageSim#decreaseAge and the backend HWC PersonValidator#extraConsistencyChecks
public class PersonPageTest {

  @Test
  public void testDecreaseAgeValid(){
    // Setup
    FlensburgMock.init();
    FlensburgMock.resetLog();

    Person p = FlensburgManager.personBuilder().age(30).nickname("Mustermann").build();
    PersonPageSim pageSim = new PersonPageSim(p);

    // Simulate user input
    pageSim.decreaseAge();

    // Check
    assertTrue(FlensburgMock.getLog().isEmpty());
  }

  @Test
  public void testDecreaseAgeInvalid(){
    // Setup
    FlensburgMock.init();
    FlensburgMock.resetLog();

    Person p = FlensburgManager.personBuilder().age(18).nickname("Mustermann").build();
    PersonPageSim pageSim = new PersonPageSim(p);

    // Simulate user input
    pageSim.decreaseAge();

    // Check
    assertEquals(1, FlensburgMock.getLog().size());
    LogEntry entry = FlensburgMock.getLog().getEntry(0);
    assertTrue(entry.getMessage().contains("Alter"));
    assertTrue(entry.getMessage().contains("unter 18"));
  }
}

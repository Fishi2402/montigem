package hardy.database;

import hardy.FlensburgManagerImpl;
import hardy.Human;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleDBTest {

  @Test
  public void testSaveAndLoadDB() {

    FlensburgManagerImpl.init();

    Human h1 = FlensburgManagerImpl.humanBuilder().age(15);
    Human h2 = FlensburgManagerImpl.humanBuilder().age(42);

    long gemid1 = h1.getGemId();
    long gemid2 = h2.getGemId();

    FlensburgManagerImpl.addHuman(h1);
    FlensburgManagerImpl.addHuman(h2);

    Assertions.assertEquals(h1.getAge(), FlensburgManagerImpl.getHumanSure(gemid1).getAge());
    Assertions.assertEquals(h2.getAge(), FlensburgManagerImpl.getHumanSure(gemid2).getAge());
  }
}

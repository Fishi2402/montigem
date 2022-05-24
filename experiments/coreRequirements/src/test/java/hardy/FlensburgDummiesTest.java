package hardy;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlensburgDummiesTest {

  @Before
  public void init(){
    Log.init();
    Log.enableFailQuick(false);
    FlensburgMock.init();
  }

  @Test
  public void testInventDummies1(){
    new FlensburgDummies().inventDummiesOne();

    assertTrue("Log not empty: " + Log.getFindings(), Log.getFindings().isEmpty());

    assertEquals(6, FlensburgManager.getCarList().size());
    assertEquals(1, FlensburgManager.getHumanList().size());

    Human h = FlensburgManager.getHumanSure(2101);
    assertEquals(21, h.getAge().intValue());

    assertEquals(5, FlensburgManager.getPersonList().size());

    Person p3101 = FlensburgManager.getPersonSure(3101);
    Person p3102 = FlensburgManager.getPersonSure(3102);
    Person p3103 = FlensburgManager.getPersonSure(3103);
    Person p3104 = FlensburgManager.getPersonSure(3104);
    Person p3105 = FlensburgManager.getPersonSure(3105);

    Car c1101 = FlensburgManager.getCarSure(1101);
    Car c1102 = FlensburgManager.getCarSure(1102);
    Car c1103 = FlensburgManager.getCarSure(1103);
    Car c1104 = FlensburgManager.getCarSure(1104);
    Car c1105 = FlensburgManager.getCarSure(1105);
    Car c1106 = FlensburgManager.getCarSure(1106);

    assertEquals(1, p3101.sizeOwnss());
    assertEquals(0, p3102.sizeOwnss());
    assertEquals(2, p3103.sizeOwnss());
    assertEquals(2, p3104.sizeOwnss());
    assertEquals(1, p3105.sizeOwnss());

    assertTrue(p3101.containsOwns(c1101));

    assertTrue(p3103.containsOwns(c1102));
    assertTrue(p3103.containsOwns(c1103));

    assertTrue(p3104.containsOwns(c1104));
    assertTrue(p3104.containsOwns(c1105));
  }

  @Test
  public void testInventDummies2(){
    int n = 10;
    new FlensburgDummies().inventDummiesTwo(n);

    assertTrue("Log not empty: " + Log.getFindings(), Log.getFindings().isEmpty());

    assertEquals(n, FlensburgManager.getCarList().size());
    assertEquals(n, FlensburgManager.getHumanList().size());
    assertEquals(n, FlensburgManager.getPersonList().size());
  }

  @Test
  public void testInventDummiesGenerated(){
    int numberOfPersonInstances = 10;
    int numberOfCarInstance = 20;
    int numberOfHumanInstances = 5;
    new FlensburgDummies().inventDummiesGenerated(numberOfPersonInstances, numberOfCarInstance, numberOfHumanInstances);

    assertTrue("Log not empty: " + Log.getFindings(), Log.getFindings().isEmpty());

    assertEquals(numberOfPersonInstances, FlensburgManager.getPersonList().size());
    assertEquals(numberOfCarInstance, FlensburgManager.getCarList().size());
    assertEquals(numberOfHumanInstances, FlensburgManager.getHumanList().size());
  }

}

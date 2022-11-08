package hardy.database;

import hardy.*;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import umlp.rte.DatabaseAccessManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class SimpleDBTest {

  @Test
  public void testSaveAndLoadDB() {

    FlensburgManagerImpl.init();

    Human h1 = FlensburgManagerImpl.humanBuilder().age(15).build().get();

    Session s = DatabaseAccessManager.getSession();
    Human h2 = s.get(Human.class, h1.getGemId());
    assertNotSame(h1, h2);
    assertEquals(h1.getAge(), h2.getAge());
    assertEquals(h1.getGemId(), h2.getGemId());
  }

  @Test
  public void testSaveAndLoadDBPerson() {
    FlensburgManagerImpl.init();

    Address a1 = FlensburgManager.addressBuilder().city("Aachen").build().get();

    Person p1 = FlensburgManager
        .personBuilder()
        .age(20)
        .nickname("John")
        .mainResidence(a1)
        .build()
        .get();

    Session s = DatabaseAccessManager.getSession();
    Person p2 = s.get(Person.class, p1.getGemId());
    assertNotSame(p1, p2);
    assertEquals(p1.getAge(), p2.getAge());
    assertEquals(p1.getGemId(), p2.getGemId());
    assertEquals(p1.getNickname(), p2.getNickname());
  }
}

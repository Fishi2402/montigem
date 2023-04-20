package restaurantorg;

import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalDateTest {

  @Test
  public void testCreatePersonWithLocalDateMock(){
    RestaurantOrgMock.init();
    LogStub.init();

    // TODO: Job is optional!
    Job j = RestaurantOrgManager.jobBuilder()
        .jobName("Bla").roleID(1).build().get();

    Person p = RestaurantOrgManager.personBuilder()
        .name("Test").lastName("Test")
        .address("Test")
        .job(j)
        .birthday(LocalDate.of(2000, 1, 1)).build().get();

    assertTrue(LogStub.getFindings().isEmpty());
  }

  @Test
  public void testCreatePersonWithLocalDateDb() throws IOException {
    RestaurantOrgManagerImpl.init();
    URL schemaUrl = getClass().getResource("/RestaurantOrg.sql");
    DatabaseAccessManager.initSchema(IOUtils.toString(schemaUrl, StandardCharsets.UTF_8));
    LogStub.init();

    // TODO: Job is optional!
    Job j = RestaurantOrgManager.jobBuilder()
        .jobName("Bla").roleID(1).build().get();

    Person p = RestaurantOrgManager.personBuilder()
        .name("Test").lastName("Test")
        .address("Test")
        .job(j)
        .birthday(LocalDate.of(2000, 1, 1)).build().get();

    PersonDAO personDAO = new PersonDAO();
    Person copy = personDAO.selectPerson(p.gemId).get();

    assertNotSame(p, copy);
    assertEquals(p.gemId, copy.gemId);
    assertEquals(p._name, copy._name);
    assertEquals(p._lastName, copy._lastName);
    assertEquals(p._address, copy._address);
    assertEquals(p._job.getGemId(), copy._job.getGemId());

    assertTrue(LogStub.getFindings().isEmpty());
  }

  @Test
  public void testCreatePersonWithLocalDateDeSerRoundtrip(){
    RestaurantOrgMock.init();
    LogStub.init();

    // TODO: Job is optional!
    Job j = RestaurantOrgManager.jobBuilder()
        .jobName("Bla").roleID(1).build().get();

    Person p = RestaurantOrgManager.personBuilder()
        .name("Test").lastName("Test")
        .address("Test")
        .job(j)
        .birthday(LocalDate.of(2000, 1, 1)).build().get();

    PersonDeSer personDeSer = new PersonDeSer();
    String ser = personDeSer.serialize(p);
    Person copy = personDeSer.deserialize(ser).get();

    assertNotSame(p, copy);
    assertEquals(p.gemId, copy.gemId);
    assertEquals(p._name, copy._name);
    assertEquals(p._lastName, copy._lastName);
    assertEquals(p._address, copy._address);
    assertEquals(p._job.getGemId(), copy._job.getGemId());

    assertTrue(LogStub.getFindings().isEmpty());
  }

}

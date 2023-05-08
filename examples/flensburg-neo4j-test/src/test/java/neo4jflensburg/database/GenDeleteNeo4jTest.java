/* (c) https://github.com/MontiCore/monticore */
package neo4jflensburg.database;

import neo4jflensburg.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenDeleteNeo4jTest extends GenAbstractNeo4jDBTest {

  @Test
  public void testNeo4jHumanDaoDelete() {
    long gemId = 1001;
    int age = 20;
    Neo4jHumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Neo4jHuman h1001 = builder.build().get();

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    dao.insertNeo4jHuman(h1001);

    dao.deleteNeo4jHuman(gemId);

    Optional<Neo4jHuman> h1001CopyOpt = dao.selectNeo4jHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isEmpty());
  }

  @Test
  public void testDeleteNeo4jHumanWithDao(){
    long gemId = 1002;
    int age = 20;
    Neo4jHumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Neo4jHuman h1001db = builder.build().get();

    Neo4jHumanDAO dao = new Neo4jHumanDAO();

    dao.insertNeo4jHuman(h1001db);
    assertTrue(dao.selectNeo4jHuman(gemId).isPresent());

    dao.deleteNeo4jHuman(gemId);
    assertTrue(dao.selectNeo4jHuman(gemId).isEmpty());
  }

  @Test
  public void testDeleteNeo4jHumanWithManager(){
    int age = 20;
    Neo4jHumanBuilder builder = Neo4jFlensburgManager.neo4jHumanBuilder();
    builder.age(age);
    Neo4jHuman h1001 = builder.build().get();

    Neo4jFlensburgManager.deleteNeo4jHuman(h1001.getGemId());
    assertTrue(Neo4jFlensburgManager.neo4jHumanOpt(h1001.getGemId()).isEmpty());

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    assertTrue(dao.selectNeo4jHuman(h1001.getGemId()).isEmpty());
  }

  @Test
  public void testAssocDeleteNavigableDirection() {

    Neo4jAddress neo4jNeo4jAddress = Neo4jFlensburgManager.neo4jAddressBuilder().city("Koeln").build().get();
    Neo4jAddress uninterestingNeo4jAddress = Neo4jFlensburgManager.neo4jAddressBuilder().city("Aachen").build().get();
    Neo4jPersonBuilder neo4jNeo4jPersonBuilder = Neo4jFlensburgManager.neo4jPersonBuilder().age(33).nickname("James Bond").mainResidence(uninterestingNeo4jAddress);

    Optional<Neo4jPerson> jamesBond = neo4jNeo4jPersonBuilder.build();

    neo4jNeo4jAddress.addVisitor(jamesBond.get());

    long neo4jNeo4jPersonId = jamesBond.get().getGemId();
    long neo4jNeo4jAddressId = neo4jNeo4jAddress.getGemId();

    Neo4jAddressDAO neo4jNeo4jAddressDAO = new Neo4jAddressDAO();
    assertTrue(neo4jNeo4jAddressDAO.selectNeo4jAddress(neo4jNeo4jAddressId).isPresent());

    Neo4jPersonDAO neo4jNeo4jPersonDAO = new Neo4jPersonDAO();
    assertTrue(neo4jNeo4jPersonDAO.selectNeo4jPerson(neo4jNeo4jPersonId).isPresent());

    AssocNeo4jPerson_neo4jPerson_mainResidence_Neo4jAddressDAO assocNeo4jPerson_neo4jNeo4jPerson_mainResidence_neo4jNeo4jAddressDAO = new AssocNeo4jPerson_neo4jPerson_mainResidence_Neo4jAddressDAO();
    assertFalse(assocNeo4jPerson_neo4jNeo4jPerson_mainResidence_neo4jNeo4jAddressDAO.selectMainResidenceIdForNeo4jPerson(neo4jNeo4jPersonId).isEmpty());

    AssocNeo4jVisitsDAO assocVisitsDAO = new AssocNeo4jVisitsDAO();
    assertFalse(assocVisitsDAO.selectVisitedIdsForVisitor(neo4jNeo4jPersonId).isEmpty());
    assertFalse(assocVisitsDAO.selectVisitorIdsForVisited(neo4jNeo4jAddressId).isEmpty());

    neo4jNeo4jAddressDAO.deleteNeo4jAddress(neo4jNeo4jAddressId);

    assertTrue(assocVisitsDAO.selectVisitedIdsForVisitor(neo4jNeo4jPersonId).isEmpty());
    assertTrue(assocVisitsDAO.selectVisitorIdsForVisited(neo4jNeo4jAddressId).isEmpty());
  }

  @Test
  public void testAssocDeletesNotNavigableDirection() {
    // TODO do we need this?
  }

  // TODO: What about cascading deletes?
  // TODO: testen ob auch links entfernt werden wenn ein objekt entfernt wird
}

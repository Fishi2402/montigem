/* (c) https://github.com/MontiCore/monticore */
package neo4jflensburg.database;

import neo4jflensburg.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GenUpdateNeo4jTest extends GenAbstractNeo4jDBTest {

  @Test
  public void testNeo4jHumanDaoSetAge() {
    long gemId = 1001;
    int age = 20;
    Neo4jHumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Neo4jHuman h1001 = builder.build().get();

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    dao.insertNeo4jHuman(h1001);

    int changedAge = 21;
    dao.updateAge(gemId, changedAge);

    Optional<Neo4jHuman> h1001CopyOpt = dao.selectNeo4jHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isPresent());

    Neo4jHuman h1001Copy = h1001CopyOpt.get();
    assertNotSame(h1001, h1001Copy);

    assertEquals(h1001.getGemId(), h1001Copy.getGemId());
    assertEquals(changedAge, h1001Copy.getAge());
  }

  @Test
  public void testNeo4jHumanLoadFromDbAndSetAge(){
    long gemId = 1002;
    int age = 20;
    Neo4jHumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Neo4jHuman h1001db = builder.build().get();

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    dao.insertNeo4jHuman(h1001db);

    Neo4jHuman h1001 = Neo4jFlensburgManager.getNeo4jHumanSure(gemId);
    assertNotSame(h1001db, h1001);
    assertEquals(gemId, h1001.getGemId());
    assertEquals(age, h1001.getAge());
  }

  @Test
  public void testNeo4jHumanSetAge(){
    int age = 20;
    Neo4jHuman h = Neo4jFlensburgManager.neo4jHumanBuilder().age(age).build().get();
    int changedAge = 21;
    h.setAge(changedAge);

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    Optional<Neo4jHuman> hCopyOpt = dao.selectNeo4jHuman(h.getGemId());
    assertTrue(hCopyOpt.isPresent());
    Neo4jHuman hCopy = hCopyOpt.get();

    assertNotSame(h, hCopy);
    assertEquals(changedAge, h.getAge());
    assertEquals(h.getGemId(), hCopy.getGemId());
  }

  @Test
  public void testNeo4jPersonAddVisited(){
    Neo4jAddress a1 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Neo4jFlensburg").build().get();
    Neo4jPersonBuilder pb = Neo4jFlensburgManager.neo4jPersonBuilder().age(20).nickname("John").mainResidence(a1);
    Neo4jPerson p = pb.build().get();

    Neo4jPersonDAO neo4jPersonDAO = new Neo4jPersonDAO();

    // Initially empty
    {
      Optional<Neo4jPerson> pCopyOpt = neo4jPersonDAO.selectNeo4jPerson(p.getGemId());
      assertTrue(pCopyOpt.isPresent());

      Neo4jPerson pCopy = pCopyOpt.get();
      assertNotSame(p, pCopy);

      assertEquals(0, pCopy.sizeVisiteds());
    }

    // Add visited
    p.addVisited(a1);

    {
      Optional<Neo4jPerson> pCopyOpt = neo4jPersonDAO.selectNeo4jPerson(p.getGemId());
      assertTrue(pCopyOpt.isPresent());

      Neo4jPerson pCopy = pCopyOpt.get();
      assertNotSame(p, pCopy);

      assertEquals(1, pCopy.sizeVisiteds());
    }
  }

  @Test
  public void testNeo4jAddressAddVisitor(){
    Neo4jAddress a1 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Neo4jFlensburg").build().get();
    Neo4jAddress a2 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Aachen").build().get();
    Neo4jPersonBuilder pb = Neo4jFlensburgManager.neo4jPersonBuilder().age(20).nickname("John").mainResidence(a1);
    Neo4jPerson p = pb.build().get();


    Neo4jAddressDAO neo4jAddressDAO = new Neo4jAddressDAO();

    // Initially empty
    {
      Optional<Neo4jAddress> a2CopyOpt = neo4jAddressDAO.selectNeo4jAddress(a2.getGemId());
      assertTrue(a2CopyOpt.isPresent());

      Neo4jAddress a2Copy = a2CopyOpt.get();
      assertNotSame(a2, a2Copy);

      assertEquals(0, a2Copy.sizeVisitors());
    }

    // Add visited
    a2.addVisitor(p);

    {
      Optional<Neo4jAddress> a2CopyOpt = neo4jAddressDAO.selectNeo4jAddress(a2.getGemId());
      assertTrue(a2CopyOpt.isPresent());

      Neo4jAddress a2Copy = a2CopyOpt.get();
      assertNotSame(a2, a2Copy);

      assertEquals(1, a2Copy.sizeVisitors());
    }
  }
}

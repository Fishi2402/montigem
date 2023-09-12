/* (c) https://github.com/MontiCore/monticore */
package neo4jflensburg.database;

import com.google.common.base.Stopwatch;
import neo4jflensburg.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GenCreateNeo4jTest extends GenAbstractNeo4jDBTest {

  @Disabled("takes almost 3 minutes")
  @Test
  public void insertManyPersons(){
    // takes really long because 40k objects are inserted plus the association between them
    // and neo4j-jdbc is slow
    Stopwatch watch1 = Stopwatch.createStarted();
    for(int i = 0; i < 20000; i++){
      Neo4jAddress a1 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Flensburg").build().get();
      Neo4jPerson p1 = Neo4jFlensburgManager.neo4jPersonBuilder().age(20).nickname("Jane").mainResidence(a1).build().get();
    }
    watch1.stop();
    Long timeComplex = watch1.elapsed(TimeUnit.MILLISECONDS);
    //System.out.println(timeComplex);

    // use direct person builder, so no associations are created
    // faster
    Neo4jPersonDAO personDAO = new Neo4jPersonDAO();
    Stopwatch watch2 = Stopwatch.createStarted();
    for(int i = 0; i < 20000; i++){
      Neo4jPerson p =  new Neo4jPersonBuilder(i);
      p.setAge(20);
      p.setNickname("John");
      personDAO.insertNeo4jPerson(p);
    }
    watch2.stop();
    Long timeSequentially = watch2.elapsed(TimeUnit.MILLISECONDS);
    //System.out.println(timeSequentially);

    // faster than inserting every person with one statement
    // acceptable time of around 2-5s
    Stopwatch watch3 = Stopwatch.createStarted();
    List<Neo4jPerson> persons = new ArrayList<>();
    for(int i = 20000; i < 40000; i++){
      Neo4jPerson p =  new Neo4jPersonBuilder(i);
      p.setAge(20);
      p.setNickname("John");
      persons.add(p);
    }
    personDAO.insertNeo4jPerson(persons);
    watch3.stop();
    Long timeBatch = watch3.elapsed(TimeUnit.MILLISECONDS);
    //System.out.println(timeBatch);

    assert(timeComplex > timeSequentially);
    assert(timeSequentially > timeBatch);
  }

  @Test
  public void testHumanDaoInsertAndSelect() {
    long gemId = 1001;
    int age = 21;
    Neo4jHumanBuilder builder = new MockHumanBuilder(gemId);
    builder.age(age);
    Neo4jHuman h1001 = builder.build().get();

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    dao.insertNeo4jHuman(h1001);

    Optional<Neo4jHuman> h1001CopyOpt = dao.selectNeo4jHuman(h1001.getGemId());
    assertTrue(h1001CopyOpt.isPresent());

    Neo4jHuman h1001Copy = h1001CopyOpt.get();
    assertNotSame(h1001, h1001Copy);

    assertEquals(h1001.getGemId(), h1001Copy.getGemId());
    assertEquals(h1001.getAge(), h1001Copy.getAge());
  }

  @Test
  public void testHumanLoadFromDb(){
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


  // Test fuer 1 zu 1 Assoziationen
  @Test
  public void testMainResidence(){
    Neo4jAddress a1 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Flensburg").build().get();
    Neo4jPerson p1 = Neo4jFlensburgManager.neo4jPersonBuilder().age(20).nickname("John").mainResidence(a1).build().get();

    Neo4jPersonDAO neo4jPersonDAO = new Neo4jPersonDAO();

    Optional<Neo4jPerson> p1CopyOpt = neo4jPersonDAO.selectNeo4jPerson(p1.getGemId());
    assertTrue(p1CopyOpt.isPresent());
    Neo4jPerson p1Copy = p1CopyOpt.get();

    assertNotSame(p1, p1Copy);
    assertEquals(p1.getGemId(), p1Copy.getGemId());
    assertEquals(p1.getAge(), p1Copy.getAge());
    assertEquals(p1.getNickname(), p1Copy.getNickname());
    assertEquals(p1.getMainResidenceGem().getGemId(), p1Copy.getMainResidenceGem().getGemId());

    assertSame(a1, p1Copy.getMainResidence());
  }

  // Same tests but with Neo4jHumanDbObserver
  @Test
  public void testHumanCreate(){
    int age = 20;
    Neo4jHuman h = Neo4jFlensburgManager.neo4jHumanBuilder().age(age).build().get();

    Neo4jHumanDAO dao = new Neo4jHumanDAO();
    Optional<Neo4jHuman> hCopyOpt = dao.selectNeo4jHuman(h.getGemId());
    assertTrue(hCopyOpt.isPresent());
    Neo4jHuman hCopy = hCopyOpt.get();

    assertNotSame(h, hCopy);
    assertEquals(age, h.getAge());
    assertEquals(h.getGemId(), hCopy.getGemId());
  }

  // Tests fuer Many to Many Assoziationen
  @Test
  public void testCreatePersonWithVisited(){
    Neo4jAddress a1 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Flensburg").build().get();
    Neo4jAddress a2 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Aachen").build().get();
    Neo4jPersonBuilder pb = Neo4jFlensburgManager.neo4jPersonBuilder().age(20).nickname("John").mainResidence(a1);
    Neo4jPerson p = pb.build().get();
    pb.addVisited(a1);
    pb.addVisited(a2);

    Neo4jPersonDAO neo4jPersonDAO = new Neo4jPersonDAO();

    Optional<Neo4jPerson> pCopyOpt = neo4jPersonDAO.selectNeo4jPerson(p.getGemId());
    assertTrue(pCopyOpt.isPresent());

    Neo4jPerson pCopy = pCopyOpt.get();
    assertNotSame(p, pCopy);

    assertEquals(2, pCopy.sizeVisiteds());
  }

  @Test
  public void testCreateNeo4jAddressWithVisitors(){
    Neo4jAddress a1 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Flensburg").build().get();

    Neo4jPersonBuilder pb1 = Neo4jFlensburgManager.neo4jPersonBuilder().age(20).nickname("John").mainResidence(a1);
    Neo4jPerson p1 = pb1.build().get();

    Neo4jPersonBuilder pb2 = Neo4jFlensburgManager.neo4jPersonBuilder().age(25).nickname("Doe").mainResidence(a1);
    Neo4jPerson p2 = pb2.build().get();

    Neo4jAddress a2 = Neo4jFlensburgManager.neo4jAddressBuilder().city("Aachen").visitorGemListIds(List.of(p1.getGemId(), p2.getGemId())).build().get();

    Neo4jAddressDAO neo4jAddressDAO = new Neo4jAddressDAO();

    Optional<Neo4jAddress> a2CopyOpt = neo4jAddressDAO.selectNeo4jAddress(a2.getGemId());
    assertTrue(a2CopyOpt.isPresent());

    Neo4jAddress a2Copy = a2CopyOpt.get();
    assertNotSame(a2, a2Copy);

    assertEquals(2, a2Copy.sizeVisitors());
  }
}

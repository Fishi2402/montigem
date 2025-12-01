package flensburg.database;

import hardy.flensburg.FlensburgDAOFactory;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.Human;
import hardy.flensburg.HumanBuilder;
import hardy.flensburg.HumanDAOInterface;
import hardy.flensburg.test_infrastructure.FlensburgNeo4JDBTestSetup;
import hardy.flensburg.test_infrastructure.FlensburgSQLDBTestSetup;
import hardy.flensburg.test_infrastructure.FlensburgTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public interface ReadDBTest extends FlensburgTestSetup {

  @DisplayName("Neo4J-ReadDBTest")
  class Neo4JReadDBTest extends FlensburgNeo4JDBTestSetup implements ReadDBTest {
    Neo4JReadDBTest() {
      super(0);
    }
  }

  @DisplayName("SQL-ReadDBTest")
  class SQLReadDBTest extends FlensburgSQLDBTestSetup implements ReadDBTest {
    SQLReadDBTest() {
      super(0);
    }
  }

  @Test
  default void readManyHumans(){
    HumanDAOInterface dao = FlensburgDAOFactory.getHumanDAO();

    dao.insertHuman(new HumanBuilder(1).age(1));
    dao.insertHuman(new HumanBuilder(2).age(2));
    dao.insertHuman(new HumanBuilder(3).age(3));
    dao.insertHuman(new HumanBuilder(4).age(4));
    dao.insertHuman(new HumanBuilder(5).age(5));
    dao.insertHuman(new HumanBuilder(6).age(6));

    List<Human> allHumans = FlensburgManager.getHumanList();

    assertEquals(6, allHumans.size());

    // Assert correct order
    for (int i = 1; i <= allHumans.size(); i++) {
      assertEquals(i,allHumans.get(i-1).getGemId());
    }
  }
}

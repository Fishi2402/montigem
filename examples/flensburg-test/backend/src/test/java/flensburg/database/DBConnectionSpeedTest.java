package flensburg.database;

import com.google.common.base.Stopwatch;
import de.se_rwth.commons.logging.Log;
import hardy.flensburg.Address;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.Person;
import hardy.flensburg.test_infrastructure.FlensburgNeo4JDBTestSetup;
import hardy.flensburg.test_infrastructure.FlensburgSQLDBTestSetup;
import hardy.flensburg.test_infrastructure.FlensburgTestSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface DBConnectionSpeedTest extends FlensburgTestSetup {

  @DisplayName("Neo4J-Speedtest")
  class Neo4JSpeedtest extends FlensburgNeo4JDBTestSetup implements DBConnectionSpeedTest {
    Neo4JSpeedtest() {
      super(0);
    }
  }

  @DisplayName("SQL-Speedtest")
  class SQLSpeedtest extends FlensburgSQLDBTestSetup implements DBConnectionSpeedTest {
    SQLSpeedtest() {
      super(0);
    }
  }

  @Disabled("Not to be executed in Pipeline")
  @Test
  default void insertManyPersons(){

    Stopwatch watch = Stopwatch.createStarted();
    for(int i = 0; i < 20000; i++){
      Address a1 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
      Person p1 = FlensburgManager.personBuilder().age(20).nickname("Jane").mainResidence(a1).build().get();
    }
    watch.stop();
    Long time = watch.elapsed(TimeUnit.MILLISECONDS);
    Log.info("Insert Many Persons took " +Duration.ofMillis(time), this.getClass().getName());
  }
}

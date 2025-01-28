/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;
import de.se_rwth.commons.logging.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GemIdManager4Neo4j {
  // thread safe, dynamic sized LinkedBlockingQueue
  private final BlockingQueue<Long> reservedGemIds = new LinkedBlockingQueue<>();
  // can be changed to any value
  private final int BATCH_SIZE = 10000;
  private final Lock refillLock = new ReentrantLock();


  public void clear(){
    reservedGemIds.clear();
  }

  public long getFreeGemId() {
    if (reservedGemIds.size() == 0) {
      boolean locked = false;
      try {
        locked = refillLock.tryLock();  // only one thread can lock to refill
        if (locked && reservedGemIds.size() == 0) {
          refillReservedGemIds();
        }
      } finally {
        if (locked) {
          refillLock.unlock();
        }
      }
    }
    try {
      return reservedGemIds.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted while getting GemID", e);
    }
  }

  private void refillReservedGemIds() {
    try (Statement stmt = DatabaseAccessManager.getConnection().createStatement()) {
      Optional<ResultSet> res = Optional.ofNullable(stmt.executeQuery("MATCH(n:METANODE) return n.gemIdCounter"));
      if (!res.isPresent()) {
        throw new RuntimeException("No GemId inside DB!");
      }

      ResultSet rs = res.get();
      rs.next();
      Long startGemId = rs.getLong(1);

      for(long i = startGemId; i < startGemId + 10000L; ++i) {
        this.reservedGemIds.add(i);
      }
    } catch (SQLException var2) {
      Log.error("JDBC DB Connection Issue: " + var2.getMessage(), var2);
    }
    try(Statement stmt2 = DatabaseAccessManager.getConnection().createStatement()) {
      stmt2.execute("MATCH(n:METANODE) SET n.gemIdCounter = n.gemIdCounter+10000");
    } catch (SQLException var2) {
      Log.error("JDBC DB Connection Issue: " + var2.getMessage(), var2);
    }
  }
}

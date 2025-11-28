/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;
import org.neo4j.driver.Session;

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
    try (Session session = DatabaseAccessManager4Neo4j.getSession()) {
      long startGemId = session.run("MATCH(n:METANODE) return n.gemIdCounter")
            .single()
            .get("n.gemIdCounter")
            .asLong();
      for(long i = startGemId; i < startGemId + 10000L; ++i) {
        this.reservedGemIds.add(i);
      }
    }
    try(Session session2 = DatabaseAccessManager4Neo4j.getSession()) {
      session2.run("MATCH(n:METANODE) SET n.gemIdCounter = n.gemIdCounter+10000");
    }
  }
}

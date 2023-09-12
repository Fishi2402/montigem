package umlp.backendrte.common;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GemIdManager4SQL {
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
    try {
      Optional<ResultSet> res = DatabaseAccessManager.executeQuery("SELECT value_bigint FROM __internal_key_value__ WHERE k = 'gemid';");;

      if(res.isPresent()){
        ResultSet rs = res.get();
        rs.next();
        Long startGemId = rs.getLong(1);

        // fill the queue with BATCH_SIZE amount of gemIds
        for(long i = startGemId; i < startGemId + BATCH_SIZE; i++){
          reservedGemIds.add(i);
        }

        // add BATCH_SIZE to the highest GemID in the DB to indicate the range for future use
        DatabaseAccessManager.executeStatement("UPDATE __internal_key_value__ SET value_bigint = value_bigint +" + BATCH_SIZE + " WHERE k = 'gemid';");

      }else{
        throw new RuntimeException("No GemId inside DB!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

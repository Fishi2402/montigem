package serverperformance;

import com.google.common.util.concurrent.AtomicDouble;
import de.se_rwth.commons.logging.LogStub;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: we might need to start clients from multiple different computers
// and aggregate the metrics
public class Benchmark {
  public static void main(String[] args) {
    LogStub.init(); // Normal logs not needed
    Benchmark b = new Benchmark(
        "ws://localhost:8081/umlp/api/command",
        50,
        1000,
        5000,
        100,
        -1,
        -1
    );
    b.run();
  }

  Random random = new Random();

  // Client config
  final String wsAddress;
  final int delayBetweenClientStarts;
  final int delayBetweenActions;
  final int maxNumberOfClients;
  final int maxAcceptableDelay;

  // Initial data
  final int numberOfInstances;
  final int numberOfLinks;

  // Rolling average
  final int rollingWindowSize = 2000;
  final Deque<Integer> recentDelays = new ArrayDeque<>();
  final AtomicDouble rollingSum = new AtomicDouble();
  final AtomicInteger executedActions = new AtomicInteger();

  AtomicBoolean running = new AtomicBoolean();

  public Benchmark(String wsAddress, int delayBetweenClientStarts, int delayBetweenActions, int maxNumberOfClients, int maxAcceptableDelay, int numberOfInstances, int numberOfLinks) {
    this.wsAddress = wsAddress;
    this.delayBetweenClientStarts = delayBetweenClientStarts;
    this.delayBetweenActions = delayBetweenActions;
    this.maxNumberOfClients = maxNumberOfClients;
    this.maxAcceptableDelay = maxAcceptableDelay;
    this.numberOfInstances = numberOfInstances;
    this.numberOfLinks = numberOfLinks;
  }

  public void run(){
    running.set(true);

    int i = 0;

    while(running.get()) {
      if(i < maxNumberOfClients) {
        createNewClient();
      }
      i++;

      // Compute rolling average
      double avgDelay;
      synchronized (recentDelays) {
        avgDelay = recentDelays.isEmpty() ? 0.0 : rollingSum.get() / recentDelays.size();
      }

      if(avgDelay > maxAcceptableDelay){
        running.set(false);
      }

      // Logging
      System.out.println("Active clients = " + Math.min(maxNumberOfClients, i)
          + "; executed actions = " + executedActions
          + "; rolling avg delay = " + avgDelay);

      silentSleep(delayBetweenClientStarts);
    }
  }

  private void createNewClient() {
    new Thread(() -> {
      try (ClientProxy client = new ClientProxy(wsAddress)) {
        silentSleep(random.nextInt(delayBetweenActions));

        while (running.get()) {
          int roundtripDelay = client.execRandomAction();
          executedActions.getAndIncrement();

          synchronized (recentDelays) {
            recentDelays.addLast(roundtripDelay);
            rollingSum.addAndGet(roundtripDelay);

            if (recentDelays.size() > rollingWindowSize) {
              int removed = recentDelays.removeFirst();
              rollingSum.addAndGet(-removed);
            }
          }

          silentSleep(Math.max(0, delayBetweenActions - roundtripDelay));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  private void silentSleep(int random) {
    try {
      Thread.sleep(random);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

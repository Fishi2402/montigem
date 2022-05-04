package hardy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultLog {
    private List<LogEntry> logEntries = new ArrayList<>();

    public void add(String message, Object... arguments) {
      logEntries.add(new LogEntry(message, Arrays.asList(arguments)));
    }

  public boolean isEmpty() {
    return logEntries.isEmpty();
  }

  public int size(){
      return logEntries.size();
  }

  public LogEntry getEntry(int index){
      return logEntries.get(index);
  }
}

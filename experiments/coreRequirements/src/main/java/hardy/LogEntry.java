package hardy;

import java.util.List;

// todo: Fehlt Erklärung , Zweck

public class LogEntry {
  private final String message;
  // Question AHe: What purpose does this serve?
  private final List<Object> arguments;

  public LogEntry(String message, List<Object> arguments) {
    this.message = message;
    this.arguments = arguments;
  }

  public String getMessage() {
    return message;
  }

  public List<Object> getArguments() {
    return arguments;
  }
}

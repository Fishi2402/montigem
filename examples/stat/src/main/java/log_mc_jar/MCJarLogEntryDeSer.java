package log_mc_jar;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

public class MCJarLogEntryDeSer extends MCJarLogEntryDeSerTOP {

  protected void serializeDuration(MCJarLogEntry obj, JsonPrinter printer) {

  }

  protected Duration deserializeDuration(JsonObject json) {
    return null;
  }

  protected void serializeReceivedTime(MCJarLogEntry obj, JsonPrinter printer) {

  }

  protected Instant deserializeReceivedTime(JsonObject json) {
    return null;
  }

  protected void serializeOrigin(MCJarLogEntry obj, JsonPrinter printer) {

  }

  protected InetAddress deserializeOrigin(JsonObject json) {
    return null;
  }
}

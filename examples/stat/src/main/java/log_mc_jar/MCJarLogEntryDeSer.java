/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import log_mc_jar.db.LoadingFromDBHelper;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

public class MCJarLogEntryDeSer extends MCJarLogEntryDeSerTOP {

  protected void serializeDuration(MCJarLogEntry obj, JsonPrinter printer) {

  }

  protected Duration deserializeDuration(JsonObject json) {
    long duration = json.getMember("duration").getAsJsonNumber().getNumberAsLong();
    return Duration.ofNanos(duration);
  }

  protected void serializeReceivedTime(MCJarLogEntry obj, JsonPrinter printer) {

  }

  protected Instant deserializeReceivedTime(JsonObject json) {

    return Instant.parse(json.getMember("receivedTime").getAsJsonString().toString());

  }

  protected void serializeOrigin(MCJarLogEntry obj, JsonPrinter printer) {

  }

  protected InetAddress deserializeOrigin(JsonObject json) {
    return LoadingFromDBHelper.readIPFromStringWithoutException(json.getMember("origin").getAsJsonString().toString());
  }
}

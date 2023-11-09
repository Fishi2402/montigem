// (c) https://github.com/MontiCore/monticore
package dtcockpit;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;
import dtcockpit.commands.Room_setTemperature;
import umlp.backendrte.command.Command;

import java.util.Optional;

public class MqttSerializer extends umlp.backendrte.service.mqtt.MqttSerializer {
  @Override public Optional<Command<?>> _deserialize(String topic, String message) {

    String[] topicParts = topic.split("/");

    if (!topicParts[1].equals("room")) {
      Log.warn("Topic '" + topicParts[1] + "' does not match '/room'");
      return Optional.empty();
    }

    if (!topicParts[2].equals("roomNumber")) {
      Log.warn("Topic '" + topicParts[1] + "' does not match a 'roomNumber'");
      return Optional.empty();
    }

    Optional<Room> room = DtcockpitManager.getRoomList().stream().filter(r -> r.getRoomNumber() == Integer.parseInt(topicParts[3])).findFirst();

    JsonElement json = JsonParser.parse(message);

    // Check if access is allowed
    String uuid = json.getAsJsonObject().getStringMember("uuid");
    // TODO Lukas: check

    int newTemperature = json.getAsJsonObject().getIntegerMember("value");

    System.out.println("Old Temp: " + room.get().getTemperature());
    return room.map(value -> new Room_setTemperature(value.getGemId(), newTemperature));
  }
}

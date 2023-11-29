// (c) https://github.com/MontiCore/monticore
package dtcockpit;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;
import dtcockpit.commands.Room_setAtmosphericPressure;
import dtcockpit.commands.Room_setHumidity;
import dtcockpit.commands.Room_setMotionDetected;
import dtcockpit.commands.Room_setTemperature;
import umlp.backendrte.command.Command;

import java.util.Optional;

public class MqttSerializer extends umlp.backendrte.service.mqtt.MqttSerializer {
  @Override public Optional<Command<?>> _deserialize(String topic, String message) {

    String[] topicParts = topic.split("/");

    String className = topicParts[1];
    String identifyingAttributeName = topicParts[2];
    String identifyingAttributeValue = topicParts[3];
    String modifiedAttributeName = topicParts[4];

    if (className.startsWith("_device")) {
      // This message is intended for a device, not for the backend
      return Optional.empty();
    }

    if (!className.equals("room")) {
      Log.warn("Topic '" + className + "' does not match '/room'");
      return Optional.empty();
    }

    if (!identifyingAttributeName.equals("roomNumber")) {
      Log.warn("Topic '" + identifyingAttributeName + "' does not match 'roomNumber'");
      return Optional.empty();
    }

    Optional<Room> room = DtcockpitManager.getRoomList().stream().filter(r -> r.getRoomNumber() == Integer.parseInt(identifyingAttributeValue)).findFirst();

    JsonElement json = JsonParser.parse(message);

    // Check if access is allowed
    String uuid = json.getAsJsonObject().getStringMember("uuid");
    // TODO Lukas: check if access is allowed for the device with this uuid

    if (modifiedAttributeName.equals("temperature")) {
      double newTemperature = json.getAsJsonObject().getDoubleMember("value");
      Log.info("Room " + identifyingAttributeValue + ": New temperature: " + newTemperature, "MQTT");
      return room.map(value -> new Room_setTemperature(value.getGemId(), newTemperature));
    }
    if (modifiedAttributeName.equals("atmosphericPressure")) {
      double newPressure = json.getAsJsonObject().getDoubleMember("value");
      Log.info("Room " + identifyingAttributeValue + ": New atmo pressure: " + newPressure, "MQTT");
      return room.map(value -> new Room_setAtmosphericPressure(value.getGemId(), newPressure));
    }
    if (modifiedAttributeName.equals("humidity")) {
      double newHumidity = json.getAsJsonObject().getDoubleMember("value");
      Log.info("Room " + identifyingAttributeValue + ": New humidity: " + newHumidity, "MQTT");
      return room.map(value -> new Room_setHumidity(value.getGemId(), newHumidity));
    }
    if (modifiedAttributeName.equals("motionDetected")) {
      int newMovement = json.getAsJsonObject().getIntegerMember("value");
      Log.info("Room " + identifyingAttributeValue + ": New movement: " + (newMovement != 0), "MQTT");
      return room.map(value -> new Room_setMotionDetected(value.getGemId(), newMovement != 0));
    }
    return Optional.empty();
  }
}

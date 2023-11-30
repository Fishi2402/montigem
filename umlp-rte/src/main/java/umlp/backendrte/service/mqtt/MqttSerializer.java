// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.service.mqtt;

import umlp.backendrte.command.Command;

import java.util.Optional;

public abstract class MqttSerializer {

  public static MqttSerializer instance;

  public abstract Optional<Command<?>> _deserialize(String topic, String message);

  public static Optional<Command<?>> deserialize(String topic, String message) {
    return instance._deserialize(topic, message);
  }
}

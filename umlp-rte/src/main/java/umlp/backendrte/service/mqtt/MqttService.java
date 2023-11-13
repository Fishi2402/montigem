// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.service.mqtt;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

public class MqttService {

  // TODO: anything but global state
  public static Mqtt5BlockingClient client;

  public void connect() {

    // TODO: Replace "localhost" with name of Docker container that executes broker
    client = Mqtt5Client.builder()
        .identifier(UUID.randomUUID().toString())
        .serverHost("dt.demos.se.rwth-aachen.de")
        .serverPort(1883)
        .buildBlocking();

    // Connect to broker
    client.connect();

    /*
    // Publish a message
    client.publishWith()
        .topic("monticore")
        .qos(MqttQos.EXACTLY_ONCE)
        .payload("montithings".getBytes())
        .send();
     */

    // Set up subscription
    client.toAsync().subscribeWith()
        .topicFilter("#")
        .qos(MqttQos.EXACTLY_ONCE)
        .callback(mqtt5Publish -> onMessage(mqtt5Publish, client.toAsync()))
        .send();
  }

  protected static void onMessage(final Mqtt5Publish mqtt5Publish, final Mqtt5AsyncClient client) {
    String topic = mqtt5Publish.getTopic().toString();
    String message = new String(mqtt5Publish.getPayloadAsBytes(), Charset.defaultCharset());
    Optional<Command<?>> cmd = MqttSerializer.deserialize(topic, message);
    cmd.ifPresent(CommandManager::executeCommand);
  }
}

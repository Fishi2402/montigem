// (c) https://github.com/MontiCore/monticore
package umlp.backendrte.service.mqtt;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

import static umlp.backendrte.service.websocket.WebSocketConfig.UMLP_DOMAINNAME_ENV;

public class MqttService {

  protected static MqttService instance;

  protected Mqtt5BlockingClient client;

  /**
   * Connects to an MQTT broker on port 1883 and subscribes to all topics.
   * The broker is either the server the application is deployed on (environment variable
   * "UMLP_DOMAINNAME_ENV") or "localhost" (if the environment variable is not set).
   * If there is no MQTT broker available, the method will return without creating an MQTT client.
   */
  public void connect() {

    String brokerHost = System.getenv().getOrDefault(UMLP_DOMAINNAME_ENV, "localhost");

    if (!isServerReachable(brokerHost, 1883)) {
      Log.warn("Expected MQTT broker '" + brokerHost + "' is unreachable on port 1883. MQTT will not be available.");
      return;
    }

    client = Mqtt5Client.builder()
        .identifier(UUID.randomUUID().toString())
        .serverHost(brokerHost)
        .serverPort(1883)
        .buildBlocking();

    // Connect to broker
    client.connect();

    // Set up subscription
    client.toAsync().subscribeWith()
        .topicFilter("#")
        .qos(MqttQos.EXACTLY_ONCE)
        .callback(mqtt5Publish -> onMessage(mqtt5Publish, client.toAsync()))
        .send();
  }

  /**
   * Callback that deserializes received MQTT messages and executes UMLP commands if necessary
   *
   * @param mqtt5Publish message received via MQTT
   * @param client MQTT client that received the message
   */
  protected static void onMessage(final Mqtt5Publish mqtt5Publish, final Mqtt5AsyncClient client) {
    try {
      String topic = mqtt5Publish.getTopic().toString();
      String message = new String(mqtt5Publish.getPayloadAsBytes(), Charset.defaultCharset());
      Optional<Command<?>> cmd = MqttSerializer.deserialize(topic, message);
      cmd.ifPresent(CommandManager::executeCommand);
    }catch (Exception e){
      Log.error("Error while executing command received via mqtt", e);
    }
  }

  /**
   * Checks if a server is reachable on a specific port.
   *
   * @param address Server address to check.
   * @param port    Port to check.
   * @return True if the server is reachable, false otherwise.
   */
  public static boolean isServerReachable(String address, int port) {
    Socket socket = new Socket();
    try {
      SocketAddress socketAddress = new InetSocketAddress(address, port);
      socket.connect(socketAddress, 2000); // Timeout parameter is set to 2000ms
      return true;
    }
    catch (IOException e) {
      return false;
    }
    finally {
      try {
        socket.close();
      }
      catch (IOException e) {
        System.out.println("Error closing the socket: " + e.getMessage());
      }
    }
  }

  public static MqttService getInstance() {
    if (instance == null) {
      instance = new MqttService();
    }
    return instance;
  }

  public Optional<Mqtt5BlockingClient> getClient() {
    if (client == null) {
      return Optional.empty();
    }
    return Optional.of(client);
  }
}

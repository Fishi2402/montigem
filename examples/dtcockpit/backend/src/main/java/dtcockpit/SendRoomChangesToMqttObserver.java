package dtcockpit;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import umlp.backendrte.service.mqtt.MqttService;

public class SendRoomChangesToMqttObserver implements RoomObserver{

  @Override
  public void notifySetTemperature(Room room, Double oldValue, Double o) {
    // TODO: correct device_uuid
    String uuid = "mock_uuid_1234";

    MqttService.getInstance().getClient().get()
        .publishWith()
        .topic("/_device/" + uuid + "/room/" + room.getRoomNumber() + "/temperature")
        .qos(MqttQos.EXACTLY_ONCE)
        .payload(o.toString().getBytes())
        .send();
  }
}

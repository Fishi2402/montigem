package gui.rooms;/* (c) https://github.com/MontiCore/monticore */

import dtcockpit.Room;
import umlp.jsweet.extension.annotation.Component;

@Component
public class RoomStats extends RoomStatsTOP {
  String temp4304 = "? °C";
  String temp4315 = "? °C";
  String temp4312 = "? °C";

  @Override
  public String getInfoLine4304BotInfo() {
    for (int i = 0; i < this.getStats().size(); i++) {
      Room room = this.getStats().get(i);
      if (room.getRoomNumber() == 4304) {
        this.temp4304 = room.getTemperature() + " °C";
        break;
      }
    }

    return this.temp4304;
  }

  @Override
  public String getInfoLine4315BotInfo() {
    for (int i = 0; i < this.getStats().size(); i++) {
      Room room = this.getStats().get(i);
      if (room.getRoomNumber() == 4315) {
        this.temp4315 = room.getTemperature() + " °C";
        break;
      }
    }

    return this.temp4315;
  }

  @Override
  public String getInfoLine4312BotInfo() {
    for (int i = 0; i < this.getStats().size(); i++) {
      Room room = this.getStats().get(i);
      if (room.getRoomNumber() == 4312) {
        this.temp4312 = room.getTemperature() + " °C";
        break;
      }
    }

    return this.temp4312;
  }

}

package demorec.service;

import demorec.BatteryPack;
import jsweet.lang.Ambient;

import java.util.function.Consumer;

@Ambient
public class BatteryPackService {

  public native void setBatteryPack(BatteryPack newPack);

  public native BatteryPack getBatteryPack();

  public native void addObserverFunction(Consumer<BatteryPack> onBatteryPackChange);
}

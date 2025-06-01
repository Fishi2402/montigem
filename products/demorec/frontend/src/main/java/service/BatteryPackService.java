package service;

import demorec.BatteryPack;

import java.util.function.Consumer;

public class BatteryPackService {

  public native void setBatteryPack(BatteryPack newPack);
  public native BatteryPack getBatteryPack();

  public native void addObserverFunction(Consumer<BatteryPack> onBatteryPackChange);
}

/* (c) https://github.com/MontiCore/monticore */
import {TimeseriesTOP} from "weather/TimeseriesTOP";

// TODO: JSweet?
export class Timeseries extends TimeseriesTOP {

  public override getMinimumTemperature() {
    let seen = false;
    let best = 0;
    for (const containedDay of this.getContainedDaysList()) {
      const minimumTemperature = containedDay.getMinimumTemperature();
      if (!seen || minimumTemperature < best) {
        seen = true;
        best = minimumTemperature;
      }
    }
    return seen ? best : NaN;
  }

  public override getMaximumTemperature() {
    let seen = false;
    let best = 0;
    for (const containedDay of this.getContainedDaysList()) {
      const maximumTemperature = containedDay.getMaximumTemperature();
      if (!seen || maximumTemperature > best) {
        seen = true;
        best = maximumTemperature;
      }
    }
    return seen ? best : NaN;
  }
}

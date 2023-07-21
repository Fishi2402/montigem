import {NextDaysTOP} from "@src/weather/NextDaysTOP";
import { Day } from "./Day";
import {WeatherManager} from "@src/weather/WeatherManager";

// TODO: JSweet?
export class NextDays extends NextDaysTOP {
  containedDays: Day[] = [];

  public constructor() {
    super();
    const today = new Date();
    today.setHours(0, 0, 0, 0)
    const until = new Date();
    until.setDate(until.getDate() + 7);

    WeatherManager.getDayList().then(daysGemList => {
      for (let i = 0; i < daysGemList.size(); i++) {
        daysGemList.get(i).then(day => {
          const cur = day.getDate();
          if (cur >= today && cur <= until) {
              this.containedDays.push(day);
          }
        });
      }
    });
  }

  public override sizeContainedDays() {
    return this.containedDays.length;
  }

  public override getContainedDays(){
    return this.containedDays;
  }

  public override getMinimumTemperature() {
    let seen = false;
    let best = 0;
    for (const containedDay of this.containedDays) {
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
    for (const containedDay of this.containedDays) {
      const maximumTemperature = containedDay.getMaximumTemperature();
      if (!seen || maximumTemperature > best) {
        seen = true;
        best = maximumTemperature;
      }
    }
    return seen ? best : NaN;
  }
}

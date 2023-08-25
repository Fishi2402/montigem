/* (c) https://github.com/MontiCore/monticore */

import { CurrentCityWeatherTOP } from '@src/gui/current_city_weather/CurrentCityWeatherTOP';
import { Directive } from '@angular/core';
import { WeatherState } from "@src/weather/WeatherState";

@Directive()
export class CurrentCityWeather extends CurrentCityWeatherTOP {

  public getGemText3Value(): string {
    return "Mostly " + this.printWeatherState();
  }

  public printWeatherState(): string {
    if (this.getCity() == null
      || this.getCity().getCurrent() == null
      || this.getCity().getCurrent().getWeatherState == null) {
      return "";
    }
    let weather: string = WeatherState[this.getCity().getCurrent().getWeatherState()].toString();
    weather = weather.toLowerCase();
    weather = weather.charAt(0).toUpperCase() + weather.slice(1);

    return weather;
  }

}



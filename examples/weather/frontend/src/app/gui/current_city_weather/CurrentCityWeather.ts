/* (c) https://github.com/MontiCore/monticore */

import { CurrentCityWeatherTOP } from '@src/gui/current_city_weather/CurrentCityWeatherTOP';
import { Component } from '@angular/core';
import { WeatherState } from "@src/weather/WeatherState";

@Component({
  selector: 'current-city-weather',
  templateUrl: '../../../../build/frontendGen/build/appgui/gui/current_city_weather/CurrentCityWeather.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: ['CurrentCityWeather.scss']
})
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



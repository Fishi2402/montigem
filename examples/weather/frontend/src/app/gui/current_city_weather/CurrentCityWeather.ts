/* (c) https://github.com/MontiCore/monticore */

import { CurrentCityWeatherComponent,config } from '@src/gui/current_city_weather/CurrentCityWeatherComponent';
import { Component } from '@angular/core';
import { WeatherState } from "@src/weather/WeatherState";

@Component({
  standalone:true,
  imports:[... config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class CurrentCityWeather extends CurrentCityWeatherComponent {

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



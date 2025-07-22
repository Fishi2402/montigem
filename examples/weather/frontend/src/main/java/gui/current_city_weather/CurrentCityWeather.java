/* (c) https://github.com/MontiCore/monticore */
package gui.current_city_weather;

import umlp.jsweet.extension.annotation.Component;
import weather.City;

@Component()
public class CurrentCityWeather extends CurrentCityWeatherTOP {
  @Override
  public String getMostlyValue(City city) {
    return "Mostly " + city.getCurrent().getWeatherState().toString();
  }
}

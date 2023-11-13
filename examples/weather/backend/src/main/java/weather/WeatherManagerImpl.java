/* (c) https://github.com/MontiCore/monticore */
package weather;

import umlp.backendrte.common.GemList;

import java.util.Comparator;


public class WeatherManagerImpl extends WeatherManagerImplTOP {
  @Override
  protected GemList<DailyForecast> _doGetDailyForecastList(){
    GemList<DailyForecast> days = super._doGetDailyForecastList();
    days.sort(Comparator.comparing(DailyForecast::getDate));
    return days;
  }
}

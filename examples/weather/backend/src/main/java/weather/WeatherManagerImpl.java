/* (c) https://github.com/MontiCore/monticore */
package weather;

import umlp.backendrte.common.GemList;
import umlp.backendrte.common.ReadOnlyGemList;

import java.util.Comparator;


public class WeatherManagerImpl extends WeatherManagerImplTOP {
  @Override
  protected GemList<DailyForecast> _doGetDailyForecastList() {
    // TODO: Handle sorted views of ReadOnlyGemLists
    GemList<DailyForecast> days = ((ReadOnlyGemList) super._doGetDailyForecastList()).getRealList();
    days.sort(Comparator.comparing(DailyForecast::getDate));
    return new umlp.backendrte.common.ReadOnlyGemList<>(days);
  }
}

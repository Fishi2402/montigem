package weather;

import umlp.backendrte.common.GemList;

import java.util.Comparator;


public class WeatherManagerImpl extends WeatherManagerImplTOP {
    protected GemList<DailyForecast> _doGetDayList(){
      GemList<DailyForecast> days = super._doGetDailyForecastList();
      days.sort(Comparator.comparing(DailyForecast::getDate));
      return days;
    }
}

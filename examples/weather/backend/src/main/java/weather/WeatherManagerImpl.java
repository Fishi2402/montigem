package weather;

import umlp.backendrte.common.GemList;

import java.util.Comparator;


public class WeatherManagerImpl extends WeatherManagerImplTOP {
    protected GemList<Day> _doGetDayList(){
      GemList<Day> days = super._doGetDayList();
      days.sort(Comparator.comparing(Day::getDate));
      return days;
    }
}

package weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

@SpringBootApplication(
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "weather.service"}
)
public class WeatherServerApplication extends WeatherServerApplicationTOP {

  public  static  void main (String[] args){
    SpringApplication.run(WeatherServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();
    Optional<City> aachen = Optional.empty();

    for (City city : WeatherManager.getCityList()) {
      if("Aachen".equals(city.getName())){
        aachen = Optional.of(city);
        break;
      }
    }

    if(aachen.isEmpty()){
      aachen = WeatherManager.cityBuilder().name("Aachen").latitude(50.775555).longitude(6.083611).build();
    }

    CityDeSer cityDeSer = new CityDeSer();
    System.out.println(cityDeSer.serialize(aachen.get()));

    if(WeatherManager.getAppList().isEmpty()){
      WeatherManager.appBuilder().currentCity(aachen.get()).build();
    }

    new WeatherReport().createWeatherForecast(aachen.get());

    ForecastDeSer forecastDeSer = new ForecastDeSer();
    WeatherManager.getDailyForecastList().stream().sorted(Comparator.comparing(DailyForecast::getDate)).forEach(day ->{
      System.out.println("Day " + day.getDate());
      System.out.println("Min " + day.getMinimumTemperature());
      System.out.println("Max " + day.getMaximumTemperature());
      System.out.println("State " + day.getWeatherState());
      System.out.println("Forecasts");

      for (Forecast dailyForecast : day.getDailyForecasts()) {
        System.out.println(forecastDeSer.serialize(dailyForecast));
      }
    });

    TimeseriesDeSer timeseriesDeSer = new TimeseriesDeSer();
    Timeseries timeseries = aachen.get()._nextDays.get();

    System.out.println("Next days for Aachen");
    System.out.print("Basic: ");
    System.out.println(timeseriesDeSer.serialize(timeseries));
    System.out.println("Calculated: ");
    System.out.println("Min: " + timeseries.getMinimumTemperature());
    System.out.println("Max: " + timeseries.getMinimumTemperature());
  }
}

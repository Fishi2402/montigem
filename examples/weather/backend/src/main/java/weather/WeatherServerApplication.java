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

    new WeatherReport().createWeatherForecast(aachen.get());

    ForecastDeSer forecastDeSer = new ForecastDeSer();
    WeatherManager.getDayList().stream().sorted(Comparator.comparing(Day::getDate)).forEach(day ->{
      System.out.println("Day " + day.getDate());
      System.out.println("Min " + day.getMinimumTemperature());
      System.out.println("Max " + day.getMaximumTemperature());
      System.out.println("State " + day.getWeatherState());
      System.out.println("Forecasts");

      for (Forecast dailyForecast : day.getDailyForecasts()) {
        System.out.println(forecastDeSer.serialize(dailyForecast));
      }
    });

  }
}

package weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
    new WeatherReport().createWeatherForecast();

    ForecastDeSer forecastDeSer = new ForecastDeSer();
    for (Forecast forecast : WeatherManager.getForecastList()) {
      System.out.println(forecastDeSer.serialize(forecast));
    }
  }
}

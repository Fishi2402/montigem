/* (c) https://github.com/MontiCore/monticore */

package weather;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.DatabaseAccessManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

// TODO: can we generate this?
@SpringBootApplication(
    // We have our own data management
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    // scanBasePackages = {"umlp.backendrte.service.websocket"}
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "weather.service"}
)
//@AutoConfiguration
public class WeatherServerApplication {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(WeatherServerApplication.class, args);
  }

  @PostConstruct
  public void init() {
    Log.enableFailQuick(false);
    Log.warn("I AM ALIVE!");
    WeatherManagerImpl.init();

    Optional<URL> optionalURL = Optional.ofNullable(WeatherServerApplication.class.getResource("/Weather.sql"));
    if(optionalURL.isEmpty()) {
      Log.error("0xAAAAAA Could not find schema for database.");
    }
    String schema = FileReaderWriter.readFromFile(optionalURL.get());
    Log.warn("Loading schema: " + schema);
    if (DatabaseAccessManager.initSchema(schema)) {
      WeatherReport report = new WeatherReport();
      report.createWeatherForecast();

      ForecastDeSer deSer = new ForecastDeSer();
      for (Forecast forecast : WeatherManager.getForecastList()) {
        System.out.println(deSer.serialize(forecast));
      }
    }
  }

}

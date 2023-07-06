/* (c) https://github.com/MontiCore/monticore */
package weather;

import de.se_rwth.commons.logging.Log;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = {"weather.shellapi"})
public class WeatherShellAPIApplication {
  public static void main(String[] args) throws Exception {
    Log.initSlf4j();
    Log.initDEBUG();
    Log.info("Status: RUNNING", "UMLP API");

    WeatherManager.initMe(new WeatherManagerClientImpl());
    WeatherCommandObjectMapper.init();

    new SpringApplicationBuilder(WeatherShellAPIApplication.class)
        .web(WebApplicationType.NONE)
        .run(args);
    Log.info("Status: FINISHED", "UMLP API");
  }
}

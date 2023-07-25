// (c) https://github.com/MontiCore/monticore
package weather;

import com.google.common.collect.Multimap;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Collectors;

public class WeatherReport {

  public void createWeatherForecast(City city) {
    JsonObject data = getWeatherData(city);
    for (int i = 0; i < data.getObjectMember("hourly").getArrayMember("time").size(); i++) {

      LocalDate date = LocalDate.parse(data.getObjectMember("hourly").getArrayMember("time").get(i).getAsJsonString()
          .getValue(), DateTimeFormatter.ISO_DATE_TIME);

      WeatherManager.forecastBuilder()
          .longitude(data.getDoubleMember("longitude"))
          .latitude(data.getDoubleMember("latitude"))
          .date(date)
          .temperature(data.getObjectMember("hourly").getArrayMember("temperature_2m").get(i).getAsJsonNumber()
              .getNumberAsDouble())
          .realtiveHumidity(data.getObjectMember("hourly").getArrayMember("relativehumidity_2m").get(i).getAsJsonNumber()
              .getNumberAsInteger())
          .windspeed(data.getObjectMember("hourly").getArrayMember("windspeed_10m").get(i).getAsJsonNumber()
              .getNumberAsDouble())
          // TODO: from data/calculate
          .weatherState(WeatherState.SUNNY)
          .build();
    }

    var grouped = WeatherManager.getForecastList().stream().collect(Collectors.groupingBy(Forecast::getDate));
    grouped.forEach((d, forecasts) ->
        WeatherManager.dailyForecastBuilder().city(city).date(d).dailyForecasts(new HashSet<>(forecasts)).build()
    );

    city.calcNextDays();
  }

  public JsonObject getWeatherData(City city) {
    try {
      URL url = new URL(String.format(Locale.ROOT,
          "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&hourly=temperature_2m,relativehumidity_2m,windspeed_10m",
          city.getLatitude(),
          city.getLongitude()
      ));
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");
      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP Error code : "
            + conn.getResponseCode());
      }
      InputStreamReader in = new InputStreamReader(conn.getInputStream());
      BufferedReader br = new BufferedReader(in);
      StringBuilder json = new StringBuilder();
      String currentLine;
      while ((currentLine = br.readLine()) != null) {
        json.append(currentLine);
      }
      conn.disconnect();
      return JsonParser.parseJsonObject(json.toString());
    } catch (Exception e) {
      Log.error("Could not retrieve weather report" + e.getLocalizedMessage());
      return null; // unreachable, only to satisfy compiler
    }
  }
}

// (c) https://github.com/MontiCore/monticore
package weather;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherReport {

  public void createWeatherForecast() {
    JsonObject data = getWeatherData();
    for (int i = 0; i < data.getObjectMember("hourly").getArrayMember("time").size(); i++) {

      LocalDateTime date = LocalDateTime.parse(data.getObjectMember("hourly").getArrayMember("time").get(i).getAsJsonString()
          .getValue(), DateTimeFormatter.ISO_DATE_TIME);

      WeatherManager.forecastBuilder()
          .longitude(data.getDoubleMember("longitude"))
          .latitude(data.getDoubleMember("latitude"))
          .date(LocalDate.now())
          .temperature(data.getObjectMember("hourly").getArrayMember("temperature_2m").get(i).getAsJsonNumber()
              .getNumberAsDouble())
          .realtiveHumidity(data.getObjectMember("hourly").getArrayMember("relativehumidity_2m").get(i).getAsJsonNumber()
              .getNumberAsInteger())
          .windspeed(data.getObjectMember("hourly").getArrayMember("windspeed_10m").get(i).getAsJsonNumber()
              .getNumberAsDouble())
          .build();
    }
  }

  public JsonObject getWeatherData() {
    try {
      URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true&hourly=temperature_2m,relativehumidity_2m,windspeed_10m");
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

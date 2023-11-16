/* (c) https://github.com/MontiCore/monticore */
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Collectors;

public class WeatherReport {

  public void createWeatherForecast(City city) {
    JsonObject data = getWeatherData(city);
    JsonObject hourly = data.getObjectMember("hourly");
    for (int i = 0; i < hourly.getArrayMember("time").size(); i++) {

      LocalDateTime date = LocalDateTime.parse(hourly.getArrayMember("time").get(i).getAsJsonString()
          .getValue(), DateTimeFormatter.ISO_DATE_TIME);

      WeatherManager.forecastBuilder()
          .longitude(data.getDoubleMember("longitude"))
          .latitude(data.getDoubleMember("latitude"))
          .date(date)
          .temperature(hourly.getArrayMember("temperature_2m").get(i).getAsJsonNumber()
              .getNumberAsDouble())
          .realtiveHumidity(hourly.getArrayMember("relativehumidity_2m").get(i).getAsJsonNumber()
              .getNumberAsInteger())
          .windspeed(hourly.getArrayMember("windspeed_10m").get(i).getAsJsonNumber()
              .getNumberAsDouble())
          .weatherState(getStateForWMOCode(hourly.getArrayMember("weathercode").get(i).getAsJsonNumber().getNumberAsInteger()))
          .build();
    }

    // Group forecasts by their Date ignoring the time
    var grouped = WeatherManager.getForecastList()
        .stream().collect(Collectors.groupingBy(f -> f.getDate().toLocalDate()));
    grouped.forEach((d, forecasts) -> WeatherManager.dailyForecastBuilder()
        .city(city)
        .date(d)
        .dailyForecasts(new HashSet<>(forecasts)).build());

    city.calcNextDays();
  }

  // https://open-meteo.com/en/docs#hourly=temperature_2m,weathercode Weather variable documentation
  public WeatherState getStateForWMOCode(int code){
    switch (code) {
      case 0:
      case 1:
      case 2: return WeatherState.SUNNY;
      case 3:
      case 45:
      case 48: return WeatherState.CLOUDY;
      case 51:
      case 53:
      case 55:
      case 56:
      case 57:
      case 61:
      case 63:
      case 65:
      case 66:
      case 67:
      case 71:
      case 73:
      case 75:
      case 77:
      case 80:
      case 81:
      case 82:
      case 85:
      case 86: return WeatherState.RAINY;
      case 95:
      case 96:
      case 99: return WeatherState.STORM;
    }

    return WeatherState.CLOUDY;
  }

  public JsonObject getWeatherData(City city) {
    try {
      URL url = new URL(String.format(Locale.ROOT,
          "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&hourly=temperature_2m,relativehumidity_2m,windspeed_10m,weathercode",
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
      System.out.println(json);
      conn.disconnect();
      return JsonParser.parseJsonObject(json.toString());
    } catch (Exception e) {
      Log.error("Could not retrieve weather report" + e.getLocalizedMessage());
      return null; // unreachable, only to satisfy compiler
    }
  }
}

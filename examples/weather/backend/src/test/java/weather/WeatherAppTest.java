package weather;/* (c) https://github.com/MontiCore/monticore */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weather.WeatherManager;
import weather.WeatherMock;
import weather.WeatherTestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeatherAppTest {
  @BeforeEach
  public void clear() {
    WeatherTestContext.clear();
  }

  @Test
  public void testSingletonName() {
    WeatherMock.dummyProvider = null;
    WeatherMock.init();


    assertEquals(WeatherManager.getApp().getAppName(), "MyFancyWeatherApp");
  }

  @Test
  public void testCityInfo() {
    WeatherMock.dummyProvider = new WeatherDummyProvider();
    WeatherMock.init();



    assertEquals(WeatherManager.getApp().getAppName(), "MyFancyWeatherApp");
  }
}

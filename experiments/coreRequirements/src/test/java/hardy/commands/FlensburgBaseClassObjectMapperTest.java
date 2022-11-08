package hardy.commands;

// HWC-T

import hardy.FlensburgMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.rte.BaseClassObjectMapper;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Testet die per TOP-Mechanismus zu BaseClassObjectMapper hinzugefügte Basisklasse java.util.Date,
// sowie die automatische Initialisierung zusammen mit dem FlensburgManager/Mock.
public class FlensburgBaseClassObjectMapperTest {
  @BeforeEach
  public void init(){
    FlensburgMock.init();
  }

  @Test
  public void testDateSer(){
    Date d = getDate();
    assertEquals("2022-11-03T12:00:00Z", BaseClassObjectMapper.serialize(d, "java.util.Date").get());
  }

  @Test
  public void testDateDeSer(){
    Date d = getDate();
    assertEquals(d, BaseClassObjectMapper.deserialize("2022-11-03T12:00:00Z", "java.util.Date").get());
  }

  private static Date getDate() {
    Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getTimeZone("gmt"));
    c.set(2022, Calendar.NOVEMBER, 3, 12, 0, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }
}

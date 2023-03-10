/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;

// HWC-T

import hardy.FlensburgMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.BaseClassObjectMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  public void testInstantDeSerRoundtrip(){
    Instant i = getDate().toInstant();
    String ser = BaseClassObjectMapper.serialize(i, "java.time.Instant").get();
    Object deser = BaseClassObjectMapper.deserialize(ser, "java.time.Instant").get();

    assertTrue(deser instanceof Instant);
    assertEquals(i, deser);
  }

  @Test
  public void testDurationDeSerRoundtrip(){
    Instant i = getDate().toInstant();
    Duration d = Duration.between(i, i.plus(30, ChronoUnit.MINUTES));
    String ser = BaseClassObjectMapper.serialize(d, "java.time.Duration").get();
    Object deser = BaseClassObjectMapper.deserialize(ser, "java.time.Duration").get();

    assertTrue(deser instanceof Duration);
    assertEquals(d, deser);
  }

  @Test
  public void testInetAddressDeSerRoundtrip() throws UnknownHostException {
    InetAddress address = InetAddress.getLocalHost();
    String ser = BaseClassObjectMapper.serialize(address, "java.net.InetAddress").get();
    Object deser = BaseClassObjectMapper.deserialize(ser, "java.net.InetAddress").get();

    assertTrue(deser instanceof InetAddress);
    assertEquals(address, deser);
  }

  private static Date getDate() {
    Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getTimeZone("gmt"));
    c.set(2022, Calendar.NOVEMBER, 3, 12, 0, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }
}

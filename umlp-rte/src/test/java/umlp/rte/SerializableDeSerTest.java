package umlp.rte;

import org.junit.jupiter.api.Test;

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

public class SerializableDeSerTest {
  private SerializableDeSer sut = new SerializableDeSer();

  @Test
  public void testInstantDeSerRoundtrip(){
    Instant i = getDate().toInstant();
    String ser = sut.serialize(i);
    Object deser = sut.deserialize(ser).get();

    assertTrue(deser instanceof Instant);
    assertEquals(i, deser);
  }

  @Test
  public void testDurationDeSerRoundtrip(){
    Instant i = getDate().toInstant();
    Duration d = Duration.between(i, i.plus(30, ChronoUnit.MINUTES));
    String ser = sut.serialize(d);
    Object deser = sut.deserialize(ser).get();

    assertTrue(deser instanceof Duration);
    assertEquals(d, deser);
  }

  @Test
  public void testInetAddressDeSerRoundtrip() throws UnknownHostException {
    InetAddress address = InetAddress.getLocalHost();
    String ser = sut.serialize(address);
    Object deser = sut.deserialize(ser).get();

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
package attributes;

import attributes.test_infrastructure.AttributesDBTestSetup;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class TypesTest extends AttributesDBTestSetup {

  public TypesTest() {
    super(0);
  }

  @Test
  public void testMinimalJavaTimeDb(){
    MinimalJavaTime minimalJavaTime = AttributesManager.minimalJavaTimeBuilder()
        .bar_local_date(LocalDate.now())
        .bar_local_time(LocalTime.now())
        .bar_local_date_time(LocalDateTime.now())
        .build().get();

    MinimalJavaTimeDAO dao = new MinimalJavaTimeDAO();
    MinimalJavaTime minimalJavaTime2 = dao.selectMinimalJavaTime(minimalJavaTime.getGemId()).get();
    assertNotSame(minimalJavaTime2, minimalJavaTime);

    assertEquals(minimalJavaTime.getBar_local_time().truncatedTo(ChronoUnit.MILLIS), minimalJavaTime2.getBar_local_time().truncatedTo(ChronoUnit.MILLIS));
    assertEquals(minimalJavaTime.getBar_local_date_time().truncatedTo(ChronoUnit.MILLIS), minimalJavaTime2.getBar_local_date_time().truncatedTo(ChronoUnit.MILLIS));
    assertEquals(minimalJavaTime.getBar_local_date(), minimalJavaTime2.getBar_local_date());
  }

  @Test
  public void testMinimalJavaTimeDeSer(){
    MinimalJavaTime minimalJavaTime = AttributesManager.minimalJavaTimeBuilder()
        .bar_local_date(LocalDate.now())
        .bar_local_time(LocalTime.now())
        .bar_local_date_time(LocalDateTime.now())
        .build().get();

    AttributesBaseClassObjectMapper.init();

    MinimalJavaTimeDeSer deSer = new MinimalJavaTimeDeSer();
    String s = deSer.serialize(minimalJavaTime);
    MinimalJavaTime minimalJavaTime2 = deSer.deserialize(s).get();
    assertNotSame(minimalJavaTime, minimalJavaTime2);

    assertEquals(minimalJavaTime.getBar_local_time().truncatedTo(ChronoUnit.MILLIS), minimalJavaTime2.getBar_local_time().truncatedTo(ChronoUnit.MILLIS));
    assertEquals(minimalJavaTime.getBar_local_date_time().truncatedTo(ChronoUnit.MILLIS), minimalJavaTime2.getBar_local_date_time().truncatedTo(ChronoUnit.MILLIS));
    assertEquals(minimalJavaTime.getBar_local_date(), minimalJavaTime2.getBar_local_date());
  }

}

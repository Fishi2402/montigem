package attributes;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypesTest {

  @BeforeEach
  public void init() throws IOException {
    AttributesManagerImpl.init();
    String schema = IOUtils.resourceToString("/Attributes.sql", StandardCharsets.UTF_8);
    assertTrue(DatabaseAccessManager.initSchema(schema));
  }

  @AfterEach
  public void close() throws SQLException {
    DatabaseAccessManager.closeConnection();
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
    System.out.println(s);
  }

}

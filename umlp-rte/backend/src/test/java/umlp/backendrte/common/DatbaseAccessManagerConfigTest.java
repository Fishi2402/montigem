package umlp.backendrte.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class DatbaseAccessManagerConfigTest {

  @BeforeEach
  public void ensureProperties() {
    // Database properties file has looks like this
    try {
      assertEquals("""
        jdbc_driver=org.h2.Driver
        database_url=jdbc:h2:mem:umlpdb
        user=admin
        password=passwort
        """,
          new String(this.getClass().getClassLoader()
              .getResourceAsStream(DatabaseAccessManager.DATABASE_PROPERTIES_STD_FILE_NAME)
              .readAllBytes(),
              StandardCharsets.UTF_8));
    }catch (IOException e) {
      fail(e);
    }
  }

  @Test
  public void checkCorrectPropertiesReading() {
    DatabaseAccessManager databaseAccessManager = new DatabaseAccessManager4SQL();
    databaseAccessManager.loadDBPropertiesFromStandardFileLocation();
    assertEquals("jdbc:h2:mem:umlpdb", DatabaseAccessManager.DB_URL);
    assertEquals("jdbc:h2:mem:umlpdb", DatabaseAccessManager.databaseProperties.getProperty("database_url"));
    assertEquals("admin", DatabaseAccessManager.databaseProperties.getProperty("user"));
    assertEquals("passwort", DatabaseAccessManager.databaseProperties.getProperty("password"));
    assertEquals("org.h2.Driver", DatabaseAccessManager.databaseProperties.getProperty("jdbc_driver"));
  }

  @Test
  public void checkPrecedenceOfEnvVariablesOverProperties() {
    String databaseURL , jdbcDriver, user, password;
    databaseURL = jdbcDriver = user = password = "test";
    Map<String, String> mockEnvironment = new HashMap<>();
    mockEnvironment.put("database_url",databaseURL);
    mockEnvironment.put("db_user",user);
    mockEnvironment.put("password",password);
    mockEnvironment.put("jdbc_driver",jdbcDriver);

    DatabaseAccessManager databaseAccessManager = new DatabaseAccessManager4SQL(mockEnvironment);
    databaseAccessManager.loadDBPropertiesFromStandardFileLocation();
    assertEquals(databaseURL, DatabaseAccessManager.DB_URL);
    assertEquals(databaseURL, DatabaseAccessManager.databaseProperties.getProperty("database_url"));
    assertEquals(user, DatabaseAccessManager.databaseProperties.getProperty("user"));
    assertEquals(password, DatabaseAccessManager.databaseProperties.getProperty("password"));
    assertEquals(jdbcDriver, DatabaseAccessManager.databaseProperties.getProperty("jdbc_driver"));
  }
}

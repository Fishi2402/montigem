package loadingtests.ocl;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.ocl.OCL2SQL;
import umlp.backendrte.ocl.OCLException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OCL2SQLTest {

  @BeforeEach
  public void setup() {
    OCL2SQL.init();
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    LoadingTestsQueryBuilderMap.init();
  }

  @Test
  public void testConvertThrows() {
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert(""));
  }

  @Test
  public void testConvert2Select() throws IOException {
    assertEquals(
        "SELECT p.id, p.name, p.age FROM person p",
        collapse(OCL2SQL.getInstance().convert("[ p | p in Person ]"))
    );

    assertEquals(
        "SELECT p.id, p.name, p.age FROM person p"
            + " WHERE"
            + " p.age>18 AND"
            + " p.age<23 AND"
            + " p.age>=15 AND"
            + " p.age<=66 AND"
            + " p.name=\"Alice\" AND"
            + " p.name!=\"Bob\"",
        collapse(OCL2SQL.getInstance().convert("[ p | p in Person,"
            + " p.age > 18,"
            + " p.age < 23,"
            + " p.age >= 15,"
            + " p.age <= 66,"
            + " p.name == \"Alice\","
            + " p.name != \"Bob\"]"))
    );

    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert("[ x * x | bar in Baz ]"));
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xB0001"));

    Log.clearFindings();
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert("[ foo | bar in Baz ]"));
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xB0002"));

    Log.clearFindings();
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert("[ foo | foo in [1, 2, 3] ]"));
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xB0003"));

    Log.clearFindings();
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert("[ foo | foo in Baz, foo in Qux ]"));
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xB0004"));

    Log.clearFindings();
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert("[ foo | foo in Baz, int x = foo.bar ]"));
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xB0005"));

    Log.clearFindings();
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert("[ foo | foo in Baz, foo.bar ? 1 : 2 ]"));
    assertEquals(1, Log.getErrorCount());
    assertTrue(Log.getFindings().get(0).getMsg().contains("0xB0006"));
  }

  /**
   * Collapse all white spaces into a single space and trim. E.g.,
   * "  This   is \n a  test\tstring.  " -> "This is a test string."
   * @param s String to collapse
   * @return Trimmed string with collapsed spaces
   */
  protected String collapse(String s) {
    return s.trim().replaceAll("\\s+", " ");
  }
}

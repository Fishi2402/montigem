package loadingtests.ocl;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  }

  @Test
  public void testConvertThrows() {
    assertThrows(OCLException.class, () -> OCL2SQL.getInstance().convert(""));
  }

  @Test
  public void testConvert2Select() throws IOException {
    assertEquals("select * from Person", OCL2SQL.getInstance().convert("[ p | p in Person ]"));
    assertEquals("select * from Animal", OCL2SQL.getInstance().convert("[ a | a in Animal ]"));

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

}

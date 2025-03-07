package loadingtests.ocl;

import loadingtests.ocl.OCL2SQL;
import loadingtests.ocl.OCLParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OCL2SQLTest {

  @BeforeEach
  public void setup() {
    OCL2SQL.init();
  }

  @Test
  public void testConvertThrows() {
    assertThrows(OCLParserException.class, () -> OCL2SQL.getInstance().convert(""));
  }

  @Test
  public void testConvert2Select() throws IOException {
    assertEquals("select * from Person", OCL2SQL.getInstance().convert("[ p | p in Person ]"));
    assertEquals("select * from Animal", OCL2SQL.getInstance().convert("[ a | a in Animal ]"));
  }

}

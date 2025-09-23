package typenames;

import org.junit.jupiter.api.Test;
import typenames.test_infrastructure.TypeNamesSQLDBTestSetup;
import umlp.test_rte.ClientSetup;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeNamesTest extends TypeNamesSQLDBTestSetup {

  public TypeNamesTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void testInteraction() {
    var now = LocalDate.now();

    //test read
    Data data = TypeNamesManager.dataBuilder().date(now).build().orElseThrow();
    assertEquals(now, data.getDate());

    //test write
    data.setDescription(Optional.of("foo"));
    assertTrue(data.getDescription().isPresent());
    assertEquals("foo", data.getDescription().get());
  }
}

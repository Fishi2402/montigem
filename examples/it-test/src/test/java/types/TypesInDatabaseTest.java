package types;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypesInDatabaseTest {

  @BeforeEach
  public void init() throws IOException {
    TypesManagerImpl.init();
    DatabaseAccessManager4SQL.init();
    DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
    String schema = IOUtils.resourceToString("/Types.sql", StandardCharsets.UTF_8);
    assertTrue(DatabaseAccessManager4SQL.initSchema(schema));
  }

  @Test
  public void testFloatTypesAreWrittenToDB(){
    FloatingPoints fp = TypesManager.floatingPointsBuilder().d1(2d).d2(3d).f1(4f).f2(5f).build().get();
    FloatingPointsDAO dao = new FloatingPointsDAO();

    Optional<FloatingPoints> fpCopyOpt = dao.selectFloatingPoints(fp.gemId);

    assertTrue(fpCopyOpt.isPresent());
    FloatingPoints fpCopy = fpCopyOpt.get();
    assertNotSame(fp, fpCopy);

    assertEquals(fp.getF1(), fpCopy.getF1(), 10e-10);
    assertEquals(fp.getF2(), fpCopy.getF2(), 10e-10);
    assertEquals(fp.getD1(), fpCopy.getD1(), 10e-10);
    assertEquals(fp.getD2(), fpCopy.getD2(), 10e-10);
  }
}

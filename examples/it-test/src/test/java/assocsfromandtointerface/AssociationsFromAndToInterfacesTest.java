package assocsfromandtointerface;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import umlp.backendrte.common.DatabaseAccessManager;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC1ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC2ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC3ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC4ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c1Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c2Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c3Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c4Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC1Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC2Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC3Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC4Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC1Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC2Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC3Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC4Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI1Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI2Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI3Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI4Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i1ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i2ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i3ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i4ImplBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssociationsFromAndToInterfacesTest {

  static abstract class TestSetup{
    public abstract void initialize();
    public void clearCacheOfRemote() {}
    public void cleanup() throws SQLException{ }
  }

  static class MockTestSetup extends TestSetup{
    @Override
    public void initialize() {
      AssocsFromAndToInterfaceMock.init();
    }
  }

  static class DBTestSetup extends TestSetup{
    @Override
    public void initialize() {
      try {
        AssocsFromAndToInterfaceManagerImpl4Test instance = new AssocsFromAndToInterfaceManagerImpl4Test();
        instance.assocsFromAndToInterfaceObservers.add(new AssocsFromAndToInterfaceDbObserver());
        AssocsFromAndToInterfaceManager.initMe(instance);
        String schema = IOUtils.resourceToString("/AssocsFromAndToInterface.sql", StandardCharsets.UTF_8);
        assertTrue(DatabaseAccessManager.initSchema(schema));
      }catch (Exception e){
        throw new RuntimeException(e);
      }
    }

    @Override
    public void clearCacheOfRemote() {
      ((AssocsFromAndToInterfaceManagerImpl4Test)AssocsFromAndToInterfaceManager.getSingleton()).clearCache();
    }

    @Override
    public void cleanup() throws SQLException {
      DatabaseAccessManager.closeConnection();
    }
  }

  static class AssocsFromAndToInterfaceManagerImpl4Test extends AssocsFromAndToInterfaceManagerImpl{
    public void clearCache(){
      knownI1Impls.clear();
      knownC1s.clear();
      knownI2Impls.clear();
      knownC2s.clear();
      knownI3Impls.clear();
      knownI4Impls.clear();
      knownAC1Impls.clear();
      knownC3s.clear();
      knownAC2Impls.clear();
      knownC4s.clear();
      knownAC3Impls.clear();
      knownAC4Impls.clear();
    }
  }

  public static List<TestSetup> getSetups() {
    return List.of(
        new MockTestSetup(),
        new DBTestSetup()
    );
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testClassToInterfaceDuringSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    I1 i1 = i1ImplBuilder().build().get();
    C1 c1 = c1Builder().i1Add(i1).build().get();

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    i1 = getI1Sure(i1.getGemId());
    c1 = getC1Sure(c1.getGemId());

    assertEquals(c1.sizeI1s(), 1);
    assertEquals(c1.getI1(0), i1);

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testClassToInterfaceAfterSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    I1 i1 = i1ImplBuilder().build().get();
    C1 c1 = c1Builder().build().get();

    assertEquals(c1.sizeI1s(), 0);
    c1.addI1(i1);

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    i1 = getI1Sure(i1.getGemId());
    c1 = getC1Sure(c1.getGemId());

    assertEquals(c1.sizeI1s(), 1);
    assertEquals(c1.getI1(0), i1);

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testInterfaceToClassDuringSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    C2 c2 = c2Builder().build().get();
    I2 i2 = i2ImplBuilder().c2Add(c2).build().get();

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    c2 = getC2Sure(c2.getGemId());
    i2 = getI2Sure(i2.getGemId());

    assertEquals(1, i2.sizeC2s());
    assertEquals(c2, i2.getC2(0));

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testInterfaceToClassAfterSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    C2 c2 = c2Builder().build().get();
    I2 i2 = i2ImplBuilder().build().get();

    assertEquals(0, i2.sizeC2s());

    i2.addC2(c2);

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    c2 = getC2Sure(c2.getGemId());
    i2 = getI2Sure(i2.getGemId());

    assertEquals(1, i2.sizeC2s());
    assertEquals(c2, i2.getC2(0));

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testInterfaceToInterfaceDuringSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    I4 i4 = i4ImplBuilder().build().get();
    I3 i3 = i3ImplBuilder().i4Add(i4).build().get();

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    i4 = getI4Sure(i4.getGemId());
    i3 = getI3Sure(i3.getGemId());

    assertEquals(1, i3.sizeI4s());
    assertEquals(i4, i3.getI4(0));

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testInterfaceToInterfaceAfterSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    I4 i4 = i4ImplBuilder().build().get();
    I3 i3 = i3ImplBuilder().build().get();

    assertEquals(0, i3.sizeI4s());

    i3.addI4(i4);

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    i4 = getI4Sure(i4.getGemId());
    i3 = getI3Sure(i3.getGemId());

    assertEquals(1, i3.sizeI4s());
    assertEquals(i4, i3.getI4(0));

    setup.cleanup();
  }


  @ParameterizedTest
  @MethodSource("getSetups")
  public void testClassToAbstractClassDuringSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    AC1 aC1 = aC1ImplBuilder().build().get();
    C3 c3 = c3Builder().aC1Add(aC1).build().get();

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    aC1 = getAC1Sure(aC1.getGemId());
    c3 = getC3Sure(c3.getGemId());

    assertEquals(c3.sizeAC1s(), 1);
    assertEquals(c3.getAC1(0), aC1);

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testClassToAbstractClassAfterSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    AC1 aC1 = aC1ImplBuilder().build().get();
    C3 c3 = c3Builder().build().get();

    assertEquals(c3.sizeAC1s(), 0);
    c3.addAC1(aC1);

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    aC1 = getAC1Sure(aC1.getGemId());
    c3 = getC3Sure(c3.getGemId());

    assertEquals(c3.sizeAC1s(), 1);
    assertEquals(c3.getAC1(0), aC1);

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testAbstractClassToClassDuringSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    C4 c4 = c4Builder().build().get();
    AC2 aC2 = aC2ImplBuilder().c4Add(c4).build().get();

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    c4 = getC4Sure(c4.getGemId());
    aC2 = getAC2Sure(aC2.getGemId());

    assertEquals(1, aC2.sizeC4s());
    assertEquals(c4, aC2.getC4(0));

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testAbstractClassToClassAfterSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    C4 c4 = c4Builder().build().get();
    AC2 aC2 = aC2ImplBuilder().build().get();

    assertEquals(0, aC2.sizeC4s());

    aC2.addC4(c4);

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    c4 = getC4Sure(c4.getGemId());
    aC2 = getAC2Sure(aC2.getGemId());

    assertEquals(1, aC2.sizeC4s());
    assertEquals(c4, aC2.getC4(0));

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testAbstractClassToAbstractClassDuringSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    AC4 aC4 = aC4ImplBuilder().build().get();
    AC3 aC3 = aC3ImplBuilder().aC4Add(aC4).build().get();

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    aC4 = getAC4Sure(aC4.getGemId());
    aC3 = getAC3Sure(aC3.getGemId());

    assertEquals(1, aC3.sizeAC4s());
    assertEquals(aC4, aC3.getAC4(0));

    setup.cleanup();
  }

  @ParameterizedTest
  @MethodSource("getSetups")
  public void testAbstractClassToAbstractClassAfterSetup(TestSetup setup) throws SQLException {
    setup.initialize();
    AC4 aC4 = aC4ImplBuilder().build().get();
    AC3 aC3 = aC3ImplBuilder().build().get();

    assertEquals(0, aC3.sizeAC4s());

    aC3.addAC4(aC4);

    // Reload from remote(if remote exists)
    setup.clearCacheOfRemote();
    aC4 = getAC4Sure(aC4.getGemId());
    aC3 = getAC3Sure(aC3.getGemId());

    assertEquals(1, aC3.sizeAC4s());
    assertEquals(aC4, aC3.getAC4(0));

    setup.cleanup();
  }

}

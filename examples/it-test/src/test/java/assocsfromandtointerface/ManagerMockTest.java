package assocsfromandtointerface;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC1ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC2ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC3ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC4ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c1Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c2Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c3Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c4Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i1ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i2ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i3ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i4ImplBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagerMockTest {

  public static List<Runnable> getInitializers(){
    return List.of(
        AssocsFromAndToInterfaceMock::init
        // TODO: Test with DB
        /*, () -> {
          try {
            AssocsFromAndToInterfaceManagerImpl.init();
            String schema = IOUtils.resourceToString("/AssocsFromAndToInterface.sql", StandardCharsets.UTF_8);
            DatabaseAccessManager.initSchema(schema);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }*/
    );
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testClassToInterfaceDuringInit(){
    I1 i1 = i1ImplBuilder().build().get();
    C1 c1 = c1Builder().i1Add(i1).build().get();

    assertEquals(c1.sizeI1s(), 1);
    assertEquals(c1.getI1(0), i1);
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testClassToInterfaceAfterInit(){
    I1 i1 = i1ImplBuilder().build().get();
    C1 c1 = c1Builder().build().get();

    assertEquals(c1.sizeI1s(), 0);
    c1.addI1(i1);

    assertEquals(c1.sizeI1s(), 1);
    assertEquals(c1.getI1(0), i1);
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testInterfaceToClassDuringInit(){
    C2 c2 = c2Builder().build().get();
    I2 i2 = i2ImplBuilder().c2Add(c2).build().get();

    assertEquals(1, i2.sizeC2s());
    assertEquals(c2, i2.getC2(0));
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testInterfaceToClassAfterInit(){
    C2 c2 = c2Builder().build().get();
    I2 i2 = i2ImplBuilder().build().get();

    assertEquals(0, i2.sizeC2s());

    i2.addC2(c2);

    assertEquals(1, i2.sizeC2s());
    assertEquals(c2, i2.getC2(0));
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testInterfaceToInterfaceDuringInit(Runnable r) {
		r.run();
    I4 i4 = i4ImplBuilder().build().get();
    I3 i3 = i3ImplBuilder().i4Add(i4).build().get();

    assertEquals(1, i3.sizeI4s());
    assertEquals(i4, i3.getI4(0));
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testInterfaceToInterfaceAfterInit(Runnable r) {
		r.run();
    I4 i4 = i4ImplBuilder().build().get();
    I3 i3 = i3ImplBuilder().build().get();

    assertEquals(0, i3.sizeI4s());

    i3.addI4(i4);

    assertEquals(1, i3.sizeI4s());
    assertEquals(i4, i3.getI4(0));
  }



  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testClassToAbstractClassDuringInit(){
    AC1 aC1 = aC1ImplBuilder().build().get();
    C3 c3 = c3Builder().aC1Add(aC1).build().get();

    assertEquals(c3.sizeAC1s(), 1);
    assertEquals(c3.getAC1(0), aC1);
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testClassToAbstractClassAfterInit(){
    AC1 aC1 = aC1ImplBuilder().build().get();
    C3 c3 = c3Builder().build().get();

    assertEquals(c3.sizeAC1s(), 0);
    c3.addAC1(aC1);

    assertEquals(c3.sizeAC1s(), 1);
    assertEquals(c3.getAC1(0), aC1);
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testAbstractClassToClassDuringInit(){
    C4 c4 = c4Builder().build().get();
    AC2 aC2 = aC2ImplBuilder().c4Add(c4).build().get();

    assertEquals(1, aC2.sizeC4s());
    assertEquals(c4, aC2.getC4(0));
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testAbstractClassToClassAfterInit(){
    C4 c4 = c4Builder().build().get();
    AC2 aC2 = aC2ImplBuilder().build().get();

    assertEquals(0, aC2.sizeC4s());

    aC2.addC4(c4);

    assertEquals(1, aC2.sizeC4s());
    assertEquals(c4, aC2.getC4(0));
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testAbstractClassToAbstractClassDuringInit(Runnable r) {
		r.run();
    AC4 aC4 = aC4ImplBuilder().build().get();
    AC3 aC3 = aC3ImplBuilder().aC4Add(aC4).build().get();

    assertEquals(1, aC3.sizeAC4s());
    assertEquals(aC4, aC3.getAC4(0));
  }

  @ParameterizedTest
  @MethodSource("getInitializers")
  public void testAbstractClassToAbstractClassAfterInit(Runnable r) {
		r.run();
    AC4 aC4 = aC4ImplBuilder().build().get();
    AC3 aC3 = aC3ImplBuilder().build().get();

    assertEquals(0, aC3.sizeAC4s());

    aC3.addAC4(aC4);

    assertEquals(1, aC3.sizeAC4s());
    assertEquals(aC4, aC3.getAC4(0));
  }

}

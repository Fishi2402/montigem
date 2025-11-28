/* (c) https://github.com/MontiCore/monticore */
package assocsfromandtointerface;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import umlp.backendrte.common.LoadingState;

import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC5ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC6ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC7ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.aC8ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c5Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c6Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c7Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.c8Builder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC5Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC6Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC7Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getAC8Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC5Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC6Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC7Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getC8Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI5Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI6Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI7Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.getI8Sure;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i5ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i6ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i7ImplBuilder;
import static assocsfromandtointerface.AssocsFromAndToInterfaceManager.i8ImplBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssociationsFromAndToInterfacesGemTest {

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testClassToInterfaceDuringSetup(int clients) throws Exception {
    // TODO: Ticket #3442
    //new AdaptedTestSetup.DbTestSetup(0),
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      I5 i5 = i5ImplBuilder().build().get();
      C5 c5 = c5Builder().i5(i5).build().get();

      // Reload from remote(if remote exists)
      setup.clearCaches();

      i5 = getI5Sure(i5.getGemId());
      c5 = getC5Sure(c5.getGemId());

      assertFalse(c5.getI5Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c5.getI5(), i5);
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testClassToInterfaceAfterSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      I5 i5 = i5ImplBuilder().build().get();
      C5 c5 = c5Builder().build().get();

      assertTrue(c5.getI5Gem().getLoadingState().equals(LoadingState.EMPTY));
      c5.setI5(i5);

      // Reload from remote(if remote exists)
      setup.clearCaches();
      i5 = getI5Sure(i5.getGemId());
      c5 = getC5Sure(c5.getGemId());

      assertFalse(c5.getI5Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c5.getI5(), i5);
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testInterfaceToClassDuringSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      C6 c6 = c6Builder().build().get();
      I6 i6 = i6ImplBuilder().c6(c6).build().get();

      // Reload from remote(if remote exists)
      setup.clearCaches();
      c6 = getC6Sure(c6.getGemId());
      i6 = getI6Sure(i6.getGemId());

      assertFalse(i6.getC6Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c6, i6.getC6());
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testInterfaceToClassAfterSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      C6 c6 = c6Builder().build().get();
      I6 i6 = i6ImplBuilder().build().get();

      assertTrue(i6.getC6Gem().getLoadingState().equals(LoadingState.EMPTY));

      i6.setC6(c6);

      // Reload from remote(if remote exists)
      setup.clearCaches();
      c6 = getC6Sure(c6.getGemId());
      i6 = getI6Sure(i6.getGemId());

      assertFalse(i6.getC6Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c6, i6.getC6());
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testInterfaceToInterfaceDuringSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      I8 i8 = i8ImplBuilder().build().get();
      I7 i7 = i7ImplBuilder().i8(i8).build().get();

      // Reload from remote(if remote exists)
      setup.clearCaches();
      i8 = getI8Sure(i8.getGemId());
      i7 = getI7Sure(i7.getGemId());

      assertFalse(i7.getI8Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(i8, i7.getI8());
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testInterfaceToInterfaceAfterSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      I8 i8 = i8ImplBuilder().build().get();
      I7 i7 = i7ImplBuilder().build().get();

      assertTrue(i7.getI8Gem().getLoadingState().equals(LoadingState.EMPTY));

      i7.setI8(i8);

      // Reload from remote(if remote exists)
      setup.clearCaches();
      i8 = getI8Sure(i8.getGemId());
      i7 = getI7Sure(i7.getGemId());

      assertFalse(i7.getI8Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(i8, i7.getI8());
    }finally {
      setup.cleanup();
    }
  }


  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testClassToAbstractClassDuringSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      AC5 aC5 = aC5ImplBuilder().build().get();
      C7 c7 = c7Builder().aC5(aC5).build().get();

      // Reload from remote(if remote exists)
      setup.clearCaches();
      aC5 = getAC5Sure(aC5.getGemId());
      c7 = getC7Sure(c7.getGemId());

      assertFalse(c7.getAC5Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c7.getAC5(), aC5);
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testClassToAbstractClassAfterSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      AC5 aC5 = aC5ImplBuilder().build().get();
      C7 c7 = c7Builder().build().get();

      assertTrue(c7.getAC5Gem().getLoadingState().equals(LoadingState.EMPTY));
      c7.setAC5(aC5);

      // Reload from remote(if remote exists)
      setup.clearCaches();
      aC5 = getAC5Sure(aC5.getGemId());
      c7 = getC7Sure(c7.getGemId());

      assertFalse(c7.getAC5Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c7.getAC5(), aC5);
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testAbstractClassToClassDuringSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      C8 c8 = c8Builder().build().get();
      AC6 aC6 = aC6ImplBuilder().c8(c8).build().get();

      // Reload from remote(if remote exists)
      setup.clearCaches();
      c8 = getC8Sure(c8.getGemId());
      aC6 = getAC6Sure(aC6.getGemId());

      assertFalse(aC6.getC8Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c8, aC6.getC8());
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testAbstractClassToClassAfterSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      C8 c8 = c8Builder().build().get();
      AC6 aC6 = aC6ImplBuilder().build().get();

      assertTrue(aC6.getC8Gem().getLoadingState().equals(LoadingState.EMPTY));

      aC6.setC8(c8);

      // Reload from remote(if remote exists)
      setup.clearCaches();
      c8 = getC8Sure(c8.getGemId());
      aC6 = getAC6Sure(aC6.getGemId());

      assertFalse(aC6.getC8Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(c8, aC6.getC8());
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testAbstractClassToAbstractClassDuringSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      AC8 aC8 = aC8ImplBuilder().build().get();
      AC7 aC7 = aC7ImplBuilder().aC8(aC8).build().get();

      // Reload from remote(if remote exists)
      setup.clearCaches();
      aC8 = getAC8Sure(aC8.getGemId());
      aC7 = getAC7Sure(aC7.getGemId());

      assertFalse(aC7.getAC8Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(aC8, aC7.getAC8());
    }finally {
      setup.cleanup();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1})
  public void testAbstractClassToAbstractClassAfterSetup(int clients) throws Exception {
    AdaptedTestSetup setup = new AdaptedTestSetup.MockTestSetup(clients);
    setup.init();
    try {
      AC8 aC8 = aC8ImplBuilder().build().get();
      AC7 aC7 = aC7ImplBuilder().build().get();

      assertTrue(aC7.getAC8Gem().getLoadingState().equals(LoadingState.EMPTY));

      aC7.setAC8(aC8);

      // Reload from remote(if remote exists)
      setup.clearCaches();
      aC8 = getAC8Sure(aC8.getGemId());
      aC7 = getAC7Sure(aC7.getGemId());

      assertFalse(aC7.getAC8Gem().getLoadingState().equals(LoadingState.EMPTY));
      assertEquals(aC8, aC7.getAC8());
    }finally {
      setup.cleanup();
    }
  }

}

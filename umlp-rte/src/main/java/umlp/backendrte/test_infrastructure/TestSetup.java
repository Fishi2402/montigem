/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.test_infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

public interface TestSetup<TestContextType> {
  /**
   * Do not override!
   * Override @link{{@link TestSetup#_init()}} instead to keep BeforeEach working.
   */
  @BeforeEach default void init() throws Exception {
    // Workaround: @BeforeEach and @AfterEach is only respected if the methods are not overwritten
    _init();
  }
  default void _init() throws Exception { }

  /**
   * Do not override!
   * Override @link{{@link TestSetup#_cleanup()} ()}} instead to keep AfterEach working.
   */
  @AfterEach default void cleanup() throws Exception {
    // Workaround: @BeforeEach and @AfterEach is only respected if the methods are not overwritten
    _cleanup();
  }
  default void _cleanup() throws Exception { }

  int getNumberOfClients();

  Optional<String> getClientName(int index);

  TestContextType getBackend();
  TestContextType getClients(int i);

  boolean hasDatabase();
}

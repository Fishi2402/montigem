package umlp.backendrte.test_infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public interface TestSetup<TestContextType> {
  @BeforeEach default void init() throws Exception {
    // Workaround: @BeforeEach and @AfterEach is only respected if the methods are not overwritten
    _init();
  }
  default void _init() throws Exception { }

  @AfterEach default void cleanup() throws Exception {
    // Workaround: @BeforeEach and @AfterEach is only respected if the methods are not overwritten
    _cleanup();
  }
  default void _cleanup() throws Exception { }

  int getNumberOfClients();

  TestContextType getBackend();
  TestContextType getClients(int i);
}

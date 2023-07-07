package umlp.backendrte.test_infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public interface TestSetup<TestContextType> {
  @BeforeEach default void init() throws Exception { }
  @AfterEach default void cleanup() throws Exception { }
  int getNumberOfClients();

  TestContextType getBackend();
  TestContextType getClients(int i);
}

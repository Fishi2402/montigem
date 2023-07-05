package umlp.backendrte.test_infrastructure;

public interface TestSetup {
  default void setup() throws Exception { }
  default void cleanup() throws Exception { }
  int getNumberOfClients();
}

package umlp.backendrte.test_infrastructure;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClientSetupTest {

  ClientSetup setup;

  @Test
  void ofNumberOfClients() {
    setup = ClientSetup.of(5);
    assertEquals(5, setup.getNumberOfClients());
    assertEquals(List.of(), setup.getClientNames());
  }

  @Test
  void testClientNames() {
    setup = ClientSetup.of("foo", "bar");
    assertEquals(2, setup.getNumberOfClients());
    assertEquals(List.of("foo", "bar"), setup.getClientNames());
  }

  @Test
  void testOfNumerOfClientsAndClientNames() {
    setup = ClientSetup.of(3, "foo", "bar");
    assertEquals(3, setup.getNumberOfClients());
    assertEquals(List.of("foo", "bar"), setup.getClientNames());

    setup = ClientSetup.of(1, "foo", "bar");
    assertEquals(2, setup.getNumberOfClients());
    assertEquals(List.of("foo", "bar"), setup.getClientNames());
  }

  @Test
  void getClientName() {
    setup = ClientSetup.of(3, "foo", "bar");
    assertEquals(Optional.empty(), setup.getClientName(-1));
    assertEquals(Optional.of("foo"), setup.getClientName(0));
    assertEquals(Optional.of("bar"), setup.getClientName(1));
    assertEquals(Optional.empty(), setup.getClientName(2));
    assertEquals(Optional.empty(), setup.getClientName(3));
  }
}

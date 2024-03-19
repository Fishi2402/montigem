package umlp.backendrte.test_infrastructure;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Describes the setup of how many clients a TestSetup shall have and if they should have names.
 */
//TODO refactor test setup and use this class
public class ClientSetup {

  protected final int numberOfClients;
  protected final List<String> clientNames;

  protected ClientSetup(int numberOfClients, List<String> clientNames) {
    this.numberOfClients = Math.max(numberOfClients, clientNames.size());
    this.clientNames = clientNames;
  }

  public static ClientSetup of(int numberOfClients) {
    return new ClientSetup(numberOfClients, List.of());
  }

  public static ClientSetup of(String ... clientNames) {
    return new ClientSetup(clientNames.length, List.of(clientNames));
  }

  public static ClientSetup of(List<String> clientNames) {
    return new ClientSetup(clientNames.size(), clientNames);
  }

  public static ClientSetup of(int numberOfClients, String ... clientNames) {
    return new ClientSetup(numberOfClients, List.of(clientNames));
  }

  public static ClientSetup of(int numberOfClients, List<String> clientNames) {
    return new ClientSetup(numberOfClients, clientNames);
  }

  /**
   * Gets how many clients are included in this setup
   *
   * @return The number of clients
   */
  public int getNumberOfClients() {
    return numberOfClients;
  }

  /**
   * Gets the names of the clients, if they have any. The returned list may have fewer elements than
   * the number of clients returned by {@link #getNumberOfClients()}, but never more.
   * <p>
   * In order to fetch the name of a client {@link #getClientName(int)} should be preferred.
   *
   * @return The names of the clients, in order
   * @see #getClientName(int)
   */
  public List<String> getClientNames() {
    return clientNames;
  }

  public Optional<String> getClientName(int clientIndex) {
    try {
      return Optional.of(getClientNames().get(clientIndex));
    } catch (IndexOutOfBoundsException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientSetup that = (ClientSetup) o;
    return numberOfClients == that.numberOfClients && Objects.equals(clientNames, that.clientNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(numberOfClients, clientNames);
  }

  @Override
  public String toString() {
    return "ClientSetup{" + "numberOfClients=" + numberOfClients + ", clientNames=" + clientNames + '}';
  }
}

/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.input;

import java.util.Optional;

public class GemTextInputValidity {
  private Optional<String> status = Optional.empty();
  private Optional<String> message = Optional.empty();

  public GemTextInputValidity(String status, String message) {
    this.status = Optional.of(status);
    this.message = Optional.of(message);
  }

  public GemTextInputValidity() {
  }

  public Optional<String> getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = Optional.of(status);
  }

  public Optional<String> getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = Optional.of(message);
  }
}

/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

public class ValidationMessage {

    private String message;

    private ValidationStatus status;

    // extend with further attributes

    public ValidationMessage(ValidationStatus status, String message) {
      this.message = message;
      this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

  public ValidationStatus getStatus() {
    return status;
  }

  public void setStatus(ValidationStatus status) {
    this.status = status;
  }

    @Override
    public String toString() {
        return "ValidationMessage{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}

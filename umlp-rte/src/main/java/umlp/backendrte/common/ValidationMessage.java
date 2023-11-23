/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import java.util.ArrayList;
import java.util.List;

public class ValidationMessage {

    private String message;

    private ValidationStatus status;

    private List<String> attributes;

    // extend with further attributes

    public ValidationMessage(ValidationStatus status, String message, List<String> attributes) {
      this.message = message;
      this.status = status;
      this.attributes = attributes;
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
                ", attributes=" + String.join(", ", attributes) +
                '}';
    }

  public List<String> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }
}

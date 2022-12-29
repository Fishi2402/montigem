/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

public class ValidationMessage {

    private String message;

    // extend with further attributes

    private ValidationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ValidationMessage create(String message) {
        return new ValidationMessage(message);
    }
}

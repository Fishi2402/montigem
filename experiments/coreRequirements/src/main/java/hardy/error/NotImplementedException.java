/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package hardy.error;

public class NotImplementedException extends RuntimeException {
  public NotImplementedException() {
  }

  public NotImplementedException(String message) {
    super(message);
  }

  public NotImplementedException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotImplementedException(Throwable cause) {
    super(cause);
  }

  public NotImplementedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  @Override
  public String getMessage() {
    return "The method " + super.getMessage() + " is not implemented.";
  }
}

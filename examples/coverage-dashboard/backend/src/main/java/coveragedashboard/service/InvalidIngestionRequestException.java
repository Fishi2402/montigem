package coveragedashboard.service;

import jsweet.lang.Erased;

/**
 * Thrown when an uploaded coverage artifact archive or its {@code manifest.json} is invalid.
 */
@Erased
public class InvalidIngestionRequestException extends RuntimeException {

  public InvalidIngestionRequestException(String message) {
    super(message);
  }

  public InvalidIngestionRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}

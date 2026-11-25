package coveragedashboard.service;

import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * Translates ingestion failures into RFC 7807 {@link ProblemDetail} HTTP responses.
 */
@RestControllerAdvice(assignableTypes = IngestionEndpoint.class)
@Erased
public class IngestionExceptionHandler {

  @ExceptionHandler(InvalidIngestionRequestException.class)
  public ProblemDetail handleInvalidRequest(InvalidIngestionRequestException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Invalid Coverage Artifact");
    return problem;
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ProblemDetail handleMissingPart(MissingServletRequestPartException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Missing Required Multipart Field");
    return problem;
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ProblemDetail handleMaxUploadSize(MaxUploadSizeExceededException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.PAYLOAD_TOO_LARGE,
        "Uploaded artifact exceeds the configured maximum upload size."
    );
    problem.setTitle("Payload Too Large");
    return problem;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex) {
    Log.error("Unexpected error during coverage artifact ingestion", ex);
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred while ingesting the coverage artifact."
    );
    problem.setTitle("Internal Server Error");
    return problem;
  }
}

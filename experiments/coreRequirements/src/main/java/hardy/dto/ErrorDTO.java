/* (c) https://github.com/MontiCore/monticore */

package hardy.dto;

import hardy.error.IMontiGemErrorCode;
import hardy.error.MontiGemError;
import hardy.error.MontiGemErrorFactory;

import java.util.Optional;

/**
 * predefined DTO for errors
 * <p>
 * use when any error occured and a valid aggregate cannot be returned
 */
public class ErrorDTO extends DTO {

  private String errorCode;

  private MontiGemError error;

  private Optional<String> userMessage;

  protected ErrorDTO() {
    super("ErrorDTO");
  }

  public ErrorDTO(String errorCode, Exception e) {
    this();
    this.errorCode = errorCode;
    this.error = MontiGemErrorFactory.exceptionCaught(e);
  }

  public ErrorDTO(MontiGemError e) {
    this();
    this.errorCode = e.getErrorCode().getCode();
    this.error = e;
  }

  public ErrorDTO(String errorCode, MontiGemError error) {
    this();
    this.errorCode = errorCode;
    this.error = error;
  }

  public ErrorDTO(IMontiGemErrorCode errorCode, String msg) {
    this();
    this.errorCode = errorCode.getCode();
    this.error = MontiGemErrorFactory.unknown(msg);
    this.userMessage = Optional.of(msg);
  }

  public ErrorDTO(String errorCode, Exception e, String message) {
    this();
    this.errorCode = errorCode;
    this.error = MontiGemErrorFactory.exceptionCaught(e);
    this.userMessage = Optional.of(message);
  }

  public ErrorDTO(MontiGemError e, String message) {
    this();
    this.errorCode = e.getErrorCode().getCode();
    this.error = e;
    this.userMessage = Optional.of(message);
  }

  public ErrorDTO(String errorCode, MontiGemError error, String message) {
    this();
    this.errorCode = errorCode;
    this.error = error;
    this.userMessage = Optional.of(message);
  }

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getMessage() {
    return error.getMessage();
  }

  public MontiGemError getError() {
    return error;
  }

  public Optional<String> getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(Optional<String> userMessage) {
    this.userMessage = userMessage;
  }

  @Override
  public boolean isSuccessful() {
    return false;
  }
}

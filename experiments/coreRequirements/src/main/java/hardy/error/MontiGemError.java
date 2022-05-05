/* (c) https://github.com/MontiCore/monticore */
package hardy.error;

// RTE-P

import hardy.error.mock.StatusType;

/**
 * TODO: Write me!
 */
public class MontiGemError extends RuntimeException {

  private IMontiGemErrorCode errorCode;

  private StatusType httpStatusCode;

  private String description;

  /**
   * Constructor for MontiGemError
   *
   * @param errorCode
   * @param httpStatusCode
   * @param description
   */
  public MontiGemError(IMontiGemErrorCode errorCode, StatusType httpStatusCode, String description) {
    super();
    this.errorCode = errorCode;
    this.httpStatusCode = httpStatusCode;
    this.description = description;
  }

  /**
   * @return errorCode
   */
  public IMontiGemErrorCode getErrorCode() {
    return this.errorCode;
  }

  /**
   * @param errorCode the errorCode to set
   */
  public void setErrorCode(MontiGemErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * @return httpStatusCode
   */
  public StatusType getHttpStatusCode() {
    return this.httpStatusCode;
  }

  /**
   * @param httpStatusCode the httpStatusCode to set
   */
  public void setHttpStatusCode(StatusType httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public String toString(boolean debugMode) {
    StringBuilder sb = new StringBuilder();
    sb.append(errorCode.getCode());
    if (debugMode) {
      sb.append(": ");
      sb.append(description);
      sb.append("! (HTTP Code ");
      sb.append(httpStatusCode);
      sb.append(")");
    }
    return sb.toString();
  }

  // TODO AHe: needed?
  /*@Override
  public String toString() {
    return toString(ConfigManager.getConfig(DevelopConfig.class).debugMode);
  }

  public JsonObject toJson() {
    return Json.createObjectBuilder()
      .add("errorCode", getErrorCode().getCode())
      .add("message", getDescription())
      .build();
  }

  public String toJsonString() {
    return toJson().toString();
  }*/

}

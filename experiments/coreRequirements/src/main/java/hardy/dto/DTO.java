/* (c) https://github.com/MontiCore/monticore */

package hardy.dto;

import hardy.error.MontiGemErrorCode;

import java.util.Optional;

/**
 * Base interface for each data set
 */
public abstract class DTO {

  protected String typeName; // used for serialization, contains the typename of the specific class

   protected Optional<String> className;

  public DTO() {
    typeName = "UnknownType";
  }

  public DTO(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getErrorCode() {
    return MontiGemErrorCode.OK.getCode();
  }

  public String getMessage() {
    return MontiGemErrorCode.OK.name();
  }

  public Optional<String> getClassName() { return className; }

  public boolean isSuccessful() {
    return true;
  }
}

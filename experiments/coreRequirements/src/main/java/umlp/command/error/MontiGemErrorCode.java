/* (c) https://github.com/MontiCore/monticore */
package umlp.command.error;

// RTE-P

// todo: Zweck unklar, rauswerfen?

import com.google.gson.annotations.SerializedName;

/**
 * TODO: Write me!
 */
public enum MontiGemErrorCode implements IMontiGemErrorCode {

  /**
   * This error denotes a failure of the deserialization of a passed JSON object.
   */
  @SerializedName("MAB0x0000") DESERIALIZE("MAB0x0000"),

  /**
   * This error denotes that loading an object form the database failed. This may be caused by a
   * non-existing primary key attribute, or an unreliable connection to the database.
   */
  @SerializedName("MAB0x0001") LOAD_FROM_DB("MAB0x0001"),

  /**
   * This error denotes that building and object failed. It can be caused, e.g., by a violation of
   * domain model constraints.
   */
  @SerializedName("MAB0x0002") BUILDER("MAB0x0002"),

  /**
   * This error denotes that storing an object in the database failed. This may be caused by
   * duplicated primary key attributes, or an unreliable connection to the database.
   */
  @SerializedName("MAB0x0003") STORE_TO_DB("MAB0x0003"),

  /**
   * This error denotes a failure of the resolving of an object with a specific ID from the
   * database.
   */
  @SerializedName("MAB0x0004") RESOLVER("MAB0x0004"),

  /**
   * This error denotes that an exception was caught, that is not been handled by a dedicated error
   * code.
   */
  @SerializedName("MAB0x0005") CAUGHT_EXCEPTION("MAB0x0005"),

  /**
   * This error denotes that the txpe is not listed in any enumeration
   */
  @SerializedName("MAB0x0006") UNKNOWN_TYPE("MAB0x0006"),
  /**
   * This error denotes that a Object is not valid
   */
  @SerializedName("MAB0x0007") NOT_VALID("MAB0x0007"),

  /**
   * This error denotes that an operation is forbidden. This may be caused, e.g., if a service that
   * is only available in development mode is called in production.
   */
  @SerializedName("MAB0x0008") FORBIDDEN_OPERATION("MAB0x0008"),

  /**
   * This error denotes that an operation is forbidden. This may be caused, e.g., if a service that
   * is only available in development mode is called in production.
   */
  @SerializedName("MAB0x0009") CONFIG_LOAD_FAILURE("MAB0x0009"),

  /**
   * This error denotes that an operation is forbidden. This may be caused, e.g., if a service that
   * is only available in development mode is called in production.
   */
  @SerializedName("MAB0x0401") UNAUTHORIZED("MAB0x0401"),

  /**
   * This error denotes that a name is not unique. This may be caused, e.g., if a name is already
   * used for another object
   */
  @SerializedName("MAB0x000B") NON_UNIQUE_NAME("MAB0x000B"),

  @SerializedName("MAB0x000E") MISSING_FIELD("MAB0x000E"),

  /**
   * The request was valid, but the server is refusing action.
   * The user might not have the necessary permissions for a resource, or may need an account of some sort.
   */
  @SerializedName("MAB0x0403") FORBIDDEN("MAB0x0403"),

  /**
   * This error denotes a failure of the deserialization of a passed JSON object.
   */
  @SerializedName("MAB0x1000") OK("MAB0x1000"),

  /**
   * This error denotes a failure of a not implemented function call
   */
  @SerializedName("MAB0xDEAD") NOT_IMPLEMENTED("MAB0xDEAD"),

  /**
   * This error denotes a Conflict with a Database
   */
  @SerializedName("MAB0xC0A1") CONFLICT("MAB0xC0A1"),

  /**
   * This error denotes an unknown error
   */
  @SerializedName("MAB0x0101") UNKNOWN("MAB0x0101"),

  /**
   * This error denoted that the Mail Properties are missing
   */
  @SerializedName("MAV0x0205") MAIL_EXCEPTION("MAB0x0205"),

  /**
   * This error denoted that an undo operation can't be executed before the do part
   */
  @SerializedName("MAV0x0206") UNDO("MAB0x0206");

  private final String code;

  MontiGemErrorCode(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return getCode();
  }
}

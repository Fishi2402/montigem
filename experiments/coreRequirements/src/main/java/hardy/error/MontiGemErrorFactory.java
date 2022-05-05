/* (c) https://github.com/MontiCore/monticore */
package hardy.error;

import hardy.command_infrastructure.impl.Command;
import hardy.error.mock.Status;

public class MontiGemErrorFactory {

  public static MontiGemError notImplemented(String name) {
    return new MontiGemError(MontiGemErrorCode.NOT_IMPLEMENTED, Status.FORBIDDEN,
      name + " not implemented");
  }

  public static MontiGemError unauthorized() {
    return new MontiGemError(MontiGemErrorCode.UNAUTHORIZED, Status.UNAUTHORIZED,
      "You are not authorized");
  }

  public static MontiGemError forbidden() {
    return new MontiGemError(MontiGemErrorCode.FORBIDDEN, Status.FORBIDDEN,
      "You do not have permission");
  }

  public static MontiGemError forbiddenSelf() {
    return new MontiGemError(MontiGemErrorCode.FORBIDDEN, Status.FORBIDDEN,
      "You can not change your own admin assignment");
  }

  public static MontiGemError forbidden(String className, long id) {
    return new MontiGemError(MontiGemErrorCode.FORBIDDEN, Status.FORBIDDEN, "Sie haben nicht die Rechte für diese Aktion");
  }

  public static MontiGemError forbidden(String className) {
    return new MontiGemError(MontiGemErrorCode.FORBIDDEN, Status.FORBIDDEN, "You do not have permission for " + className);
  }

  public static MontiGemError deserializeError(Object o) {
    return new MontiGemError(MontiGemErrorCode.DESERIALIZE, Status.BAD_REQUEST,
      "Failed to deserialize object: " + o);
  }

  public static MontiGemError unknownTypeError(Object o) {
    return new MontiGemError(MontiGemErrorCode.UNKNOWN_TYPE, Status.BAD_REQUEST,
      "Failed to find type: " + o);
  }

  public static MontiGemError loadIDError(String className, long id) {
    return new MontiGemError(MontiGemErrorCode.LOAD_FROM_DB, Status.BAD_REQUEST,
      "Failed to find " + className + " with id " + id);
  }

  public static MontiGemError loadIDError(Object o) {
    return new MontiGemError(MontiGemErrorCode.LOAD_FROM_DB, Status.BAD_REQUEST,
      "Failed to find object: " + o);
  }

  public static MontiGemError storeObjectError(Object o) {
    return new MontiGemError(MontiGemErrorCode.STORE_TO_DB, Status.NOT_ACCEPTABLE,
      "Failed to store object: " + o);
  }

  public static MontiGemError builderError(Object o) {
    return new MontiGemError(MontiGemErrorCode.BUILDER, Status.NOT_ACCEPTABLE,
      "Failed to build object: " + o);
  }

  public static MontiGemError resolverError(Object o) {
    return new MontiGemError(MontiGemErrorCode.RESOLVER, Status.NOT_ACCEPTABLE,
      "Failed to resolve associations for object: " + o);
  }

  public static MontiGemError validationError(String validationMessage) {
    return new MontiGemError(MontiGemErrorCode.NOT_VALID, Status.NOT_ACCEPTABLE,
      validationMessage);
  }

  public static MontiGemError exceptionCaught(Exception e) {
    return new MontiGemError(MontiGemErrorCode.RESOLVER, Status.NOT_ACCEPTABLE,
      "The following exception occured: " + e.getMessage());
  }

  public static MontiGemError forbiddenInMode(Object o) {
    return new MontiGemError(MontiGemErrorCode.FORBIDDEN_OPERATION, Status.FORBIDDEN,
      "The operation is forbidden. It is only available in developer mode: " + o);
  }

  public static MontiGemError nameIsNotUnique(Object o) {
    return new MontiGemError(MontiGemErrorCode.NON_UNIQUE_NAME, Status.NOT_ACCEPTABLE,
      "The name is not unique and is already in use: " + o);
  }

  public static MontiGemError missingField(Object o) {
    return new MontiGemError(MontiGemErrorCode.MISSING_FIELD, Status.EXPECTATION_FAILED,
      "A field was missing: " + o);
  }

  public static MontiGemError unknown(String msg) {
    return new MontiGemError(MontiGemErrorCode.UNKNOWN, Status.NOT_ACCEPTABLE, msg);
  }

  public static MontiGemError conflict() {
    return new MontiGemError(MontiGemErrorCode.CONFLICT, Status.SERVICE_UNAVAILABLE, "The Database is not available");
  }

  public static MontiGemError mailException() {
    return new MontiGemError(MontiGemErrorCode.MAIL_EXCEPTION, Status.NOT_ACCEPTABLE, " Die angegebene Mail Adresse ist nicht korrekt oder die Mail Properties sind nicht vorhanden");
  }

  public static MontiGemError undoError(String code, Command command) {
    return new MontiGemError(MontiGemErrorCode.UNDO, Status.NOT_ACCEPTABLE, code + ": Command " + command + " cannot be undone before being called at least once");
  }
}

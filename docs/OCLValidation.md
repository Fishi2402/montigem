# Validation with OCL Rules

Steps to validate a use case:

1. Add `ocl = file("../models/ocl")` (in case ocl models are in the `models` folder of the project) to the `generateUMPLBackend`-Task of the `build.gradle` of the module
2. Create an .ocl file for the use case (for example a User.ocl for a domain object called User)
3. Add an ocl rule to the User.ocl
4. To validate the use case:
   1. Validation logic will be generated into commands (in the method `doAction()`).
   2. Manually validate by calling the validator (example for a User validation in a handwritten command `User_create.java` copied from `User_createTOP.java`):
       ```java
         UserValidator validator = new UserValidator();
         List<ValidationMessage> messages = validator.validate(user);
         if (messages.stream().anyMatch(m -> m.getStatus() == ValidationStatus.ERROR)) {
           Log.error("Validation failed for user:\n" + messages.stream().filter(m -> m.getStatus() == ValidationStatus.ERROR).map(ValidationMessage::getMessage).collect(Collectors.joining("\n")));
           return StatusResult.error(this.getId());
         }
       ```
5. If the validation fails the error message will be logged and a StatusResult.error will be returned
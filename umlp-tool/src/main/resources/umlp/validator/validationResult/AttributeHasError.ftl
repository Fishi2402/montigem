<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "attribute")}
${cd4c.method("public boolean ${attribute}ValidationMessagesHasError()")}
for (ValidationMessage message : ${attribute}ValidationMessages) {
  if(message.getStatus() == ValidationStatus.ERROR) {
    return true;
  }
}
return false;

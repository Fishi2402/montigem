<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName")}
${cd4c.constructor("public ${initializationTypeName}(Consumer<Void> consumer)")}
  this.consumer = consumer;

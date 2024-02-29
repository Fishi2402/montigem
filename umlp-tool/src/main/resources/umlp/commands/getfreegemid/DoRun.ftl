<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modelName")}
${cd4c.method("public SimpleResult<Long> doAction()")}
return new SimpleResult<>(getId(), ${modelName}Manager.getFreeGemId(), "java.lang.Long");

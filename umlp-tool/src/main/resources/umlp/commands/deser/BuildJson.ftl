<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "command")}
${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}

super.buildJson(command, jsonPrinter);
jsonPrinter.memberJson("${initializationTypeName?uncap_first}", new ${initializationTypeName}DeSer().serialize(command.get${initializationTypeName}()));

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "command")}
${cd4c.method("public void buildJson(${command} command, any jsonPrinter)")}

super.buildJson(command, jsonPrinter);
jsonPrinter["${initializationTypeName?uncap_first}"] = JSON.parse(new ${initializationTypeName}DeSer().serialize(command.get${initializationTypeName}()));

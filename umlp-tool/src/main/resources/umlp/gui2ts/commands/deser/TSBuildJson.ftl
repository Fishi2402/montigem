<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "command")}
${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}

super.buildJson(command, jsonPrinter);
jsonPrinter.memberJson("${className?uncap_first}", new ${className}DeSer().serialize(command.get${className}()));

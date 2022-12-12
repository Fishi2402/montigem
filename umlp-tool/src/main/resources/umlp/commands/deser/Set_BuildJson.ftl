<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "getterName")}
${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}

super.buildJson(command, jsonPrinter);
jsonPrinter.member("gemId", command.getGemId());
jsonPrinter.member("${attrName}", command.${getterName}());

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "command")}
${cd4c.method("public void buildJson(${command} command, any jsonPrinter)")}

super.buildJson(command, jsonPrinter);
jsonPrinter["${className?uncap_first}"] = JSON.parse(new ${className}DeSer().serialize(command.get${className}()));

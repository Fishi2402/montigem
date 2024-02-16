<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "params", "returnType", "call", "asType")}
${cd4c.method("public static ${returnType} execute${name}(${params})")}

return CommandManager.executeCommand(new ${name}(${call})).as${asType}();

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command")}
${cd4c.method("public void buildJson(${command} command, any printer)")}

super.buildJson(command, printer);
printer["gemId"] = command.getGemId();

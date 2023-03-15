<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "getterName", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("public abstract void buildJson(${command} command, JsonPrinter jsonPrinter)")}
<#else>
${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}
  super.buildJson(command, jsonPrinter);
  jsonPrinter.member("gemId", command.getGemId());
<#if useDeSer>
  jsonPrinter.member("${attrName}", ${objectMapperName}.serialize(command.${getterName}(), "${type}"));
<#else>
  jsonPrinter.member("${attrName}", command.${getterName}());
</#if>
</#if>
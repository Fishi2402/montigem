<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isCDEnum", "getterName", "useDeSer",
               "objectMapperName", "makeAbstract", "isOptional")}
<#if makeAbstract>
${cd4c.method("public abstract void buildJson(${command} command, JsonPrinter jsonPrinter)")}
<#else>
${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}
  super.buildJson(command, jsonPrinter);
  jsonPrinter.member("gemId", command.getGemId());
<#if isOptional>
  if (command.isPresent${attrName?cap_first}())
</#if>
<#if useDeSer>
  jsonPrinter.memberJson("${attrName}", ${objectMapperName}.serialize(command.${getterName}(), "${type}").orElseThrow());
<#else>
  jsonPrinter.member("${attrName}", command.${getterName}()<#if isCDEnum>.name()</#if>);
</#if>
</#if>

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isCDEnum", "getterName", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("public abstract void buildJson(${command} command, any jsonPrinter)")}
<#else>
${cd4c.method("public void buildJson(${command} command, any jsonPrinter)")}
  super.buildJson(command, jsonPrinter);
  jsonPrinter["gemId"] = command.getGemId();
<#if useDeSer>
  jsonPrinter["${attrName}"] = ${objectMapperName}.serialize(command.${getterName}(), "${type}");
<#else>
  jsonPrinter["${attrName}"] = command.${getterName}()<#if isCDEnum>.toString()</#if>;
</#if>
</#if>

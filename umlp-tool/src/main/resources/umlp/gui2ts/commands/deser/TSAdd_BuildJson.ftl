<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isCDEnum" "getterName", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("public abstract void buildJson(${command} command, any printer)")}
<#else>
${cd4c.method("public void buildJson(${command} command, any printer)")}
  super.buildJson(command, printer);
  printer["gemId"] = command.getGemId();
<#if useDeSer>
  printer["${attrName}"] = ${objectMapperName}.serialize(command.${getterName}(), "${type}"));
<#else>
  printer["${attrName}"] = command.${getterName}()<#if isCDEnum>.toString()</#if>;
</#if>
  printer["indexInList"] = command.getIndexInList();
</#if>

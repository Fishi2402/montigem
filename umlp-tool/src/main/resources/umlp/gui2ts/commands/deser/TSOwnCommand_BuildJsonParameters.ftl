<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "typeName", "isCDEnum", "json", "useDeSer", "objectMapperName", "isGemList")}
<#if useDeSer>
  jsonPrinter["${attrName}"] = ${objectMapperName}.serialize(command.${genService.getGetterName(attrName, type)}(), "${typeName}");
<#else>
  jsonPrinter["${attrName}"] = command.${genService.getGetterName(attrName, type)}<#if isGemList>GemId</#if>()<#if isCDEnum>.name()</#if>;
</#if>

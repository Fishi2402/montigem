<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "typeName", "isCDEnum", "json", "useDeSer", "objectMapperName")}
<#if useDeSer>
  jsonPrinter["${attrName}"] = ${objectMapperName}.serialize(command.${genService.getGetterName(attrName, type)}(), "${typeName}");
<#else>
  jsonPrinter["${attrName}"] = command.${genService.getGetterName(attrName, type)}()<#if isCDEnum>.name()</#if>;
</#if>

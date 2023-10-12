<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "typeName", "isCDEnum", "json", "useDeSer", "objectMapperName")}
<#if useDeSer>
  jsonPrinter.memberJson("${attrName}", ${objectMapperName}.serialize(command.${genService.getGetterName(attrName, type)}(), "${typeName}").orElseThrow());
<#else>
  jsonPrinter.member("${attrName}", command.${genService.getGetterName(attrName, type)}<#if genService.isList(type)>List</#if>()<#if isCDEnum>.name()</#if>);
</#if>

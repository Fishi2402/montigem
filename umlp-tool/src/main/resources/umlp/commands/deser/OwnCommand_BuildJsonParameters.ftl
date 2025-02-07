<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "typeName", "isCDEnum", "json", "useDeSer", "objectMapperName")}
<#if useDeSer>
  jsonPrinter.memberJson("${attrName}", ${objectMapperName}.serialize(command.${genService.getGetterName(attrName, type)}(), "${typeName}").orElseThrow());
<#else>
  <#if genService.isListOfGemIdentifiable(type)>
  jsonPrinter.beginArray("${attrName}");
  for(long gemId : command.${genService.getGetterName(attrName, type)}()) {
    jsonPrinter.value(gemId);
  }
    jsonPrinter.endArray();
  <#elseif genService.isOptional(type)>
  if (command.isPresent${attrName?cap_first}()) {
    jsonPrinter.member("${attrName}", command.${genService.getGetterName(attrName, type)}());
  }
  <#else>
    jsonPrinter.member("${attrName}", command.${genService.getGetterName(attrName, type)}<#if genService.isList(type)>List</#if>()<#if isCDEnum>.name()</#if>);
  </#if>
</#if>

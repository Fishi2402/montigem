<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("className", "attr", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
<#assign fieldName=attr.getName()>
<#if !jsonStr?has_content && !useDeSer>
<#--    TODO Kommentar einfuegen, wie das zu fixen waere-->
    ${cd4c.method("abstract protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}
<#else>
    ${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}

    <#if isList>
      printer.beginArray("${fieldName}");
      <#if useDeSer>
      obj.get${fieldName?cap_first}().stream().forEach(e -> ${objectMapperName}.serialize(printer.value(e), "${innerType}));
      <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
      obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e.name()));
      <#else>
      obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e));
      </#if>
      printer.endArray();
    <#elseif isOptional>
      if (obj.get${fieldName?cap_first}().isPresent()) {
        <#if useDeSer>
        printer.memberJson("${fieldName}", ${objectMapperName}.serialize(obj.get${fieldName?cap_first}(), "${innerType}).get());
        <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
        printer.member("${fieldName}", obj.get${fieldName?cap_first}().get().name());
        obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e.name()));
        <#else>
        printer.member("${fieldName}", obj.get${fieldName?cap_first}().get());
        obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e));
        </#if>
      }
    <#else>
      <#if useDeSer>
      printer.memberJson("${fieldName}", ${objectMapperName}.serialize(obj.${genService.getGetterName(fieldName, attr.getSymbol().getType())}(), "${fieldType}").orElseThrow());
      <#else>
      printer.member("${fieldName}", obj.${genService.getGetterName(fieldName, attr.getSymbol().getType())}()<#if genService.isCDEnum(attr.getSymbol().getType())>.name()</#if>);
      </#if>
    </#if>
</#if>

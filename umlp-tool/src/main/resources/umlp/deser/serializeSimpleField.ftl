<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("className", "fieldName", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
<#if !jsonStr?has_content && !useDeSer>
<#--    TODO Kommentar einfuegen, wie das zu fixen waere-->
    ${cd4c.method("abstract protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}
<#else>
    ${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}

    <#if isList>
      printer.beginArray("${fieldName}");
      <#if useDeSer>
      obj.get${fieldName?cap_first}().stream().forEach(e -> ${objectMapperName}.serialize(printer.value(e), "${innerType}));
      <#else>
      obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e));
      </#if>
      printer.endArray();
    <#elseif isOptional>
      if (obj.get${fieldName?cap_first}().isPresent()) {
        <#if useDeSer>
        printer.member("${fieldName}", ${objectMapperName}.serialize(obj.get${fieldName?cap_first}(), "${innerType}).get());
        <#else>
        printer.member("${fieldName}", obj.get${fieldName?cap_first}().get());
        obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e));
        </#if>
      }
    <#else>
      <#if useDeSer>
      printer.member("${fieldName}", ${objectMapperName}.serialize(obj.get${fieldName?cap_first}(), "${fieldType}"));
      <#else>
      printer.member("${fieldName}", obj.get${fieldName?cap_first}());
      </#if>
    </#if>
</#if>
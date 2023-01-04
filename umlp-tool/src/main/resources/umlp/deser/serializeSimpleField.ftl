<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "isStandard", "isList", "isOptional")}
${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}

<#if isList>
  printer.beginArray("${fieldName}");
  obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e));
  printer.endArray();
<#elseif isOptional>
  if (obj.get${fieldName?cap_first}().isPresent()) {
    printer.member("${fieldName}", obj.get${fieldName?cap_first}().get());
  }
<#elseif isStandard>
  printer.member("${fieldName}", obj.get${fieldName?cap_first}());
</#if>
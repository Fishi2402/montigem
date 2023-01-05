<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "jsonStr", "isList", "isOptional")}
<#if !jsonStr?has_content>
  ${cd4c.method("abstract protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}
<#else>
  ${cd4c.method("protected void serialize" + fieldName?cap_first + "(" + className + " obj, JsonPrinter printer)")}

  <#if isList>
    printer.beginArray("${fieldName}");
    obj.get${fieldName?cap_first}().stream().forEach(e -> printer.value(e));
    printer.endArray();
  <#elseif isOptional>
    if (obj.get${fieldName?cap_first}().isPresent()) {
      printer.member("${fieldName}", obj.get${fieldName?cap_first}().get());
    }
  <#else>
    printer.member("${fieldName}", obj.get${fieldName?cap_first}());
  </#if>
</#if>
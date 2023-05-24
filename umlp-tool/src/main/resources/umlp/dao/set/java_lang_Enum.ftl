${signature("statement","attributeName", "index", "isOptional")}
<#if isOptional>
  if (${columnName}.isPresent()) {
    ${statement}.setString(${index}, ${columnName}.get().name());
  } else {
    ${statement}.setString(${index}, null);
  }
<#else>
  ${statement}.setString(${index}, ${columnName}.name())
</#if>

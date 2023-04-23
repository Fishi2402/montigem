<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "componentName", "variableType", "variableName", "useIterate")}
${cd4c.method("public void ${methodName}(${variableType} ${variableName}, String msg)")}
  <#if useIterate>
    ${componentName}ValueError.put(${variableName}.getGemId(), msg);
  <#else>
    ${componentName}ValueError = msg;
  </#if>

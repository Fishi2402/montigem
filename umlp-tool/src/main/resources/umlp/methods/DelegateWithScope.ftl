<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "hasReturnValue")}

<#if hasReturnValue>
  return <#rt>
</#if>
${ast.getName()}(<#list ast.getCDParameterList() as p>${p.getName()}, </#list> NotificationScope.ALL);

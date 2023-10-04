<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "hasReturnValue")}

<#if hasReturnValue>
  return <#rt>
</#if>
this.${ast.getName()}1(<#list ast.getCDParameterList() as p>${p.getName()}, </#list> NotificationScope.ALL);

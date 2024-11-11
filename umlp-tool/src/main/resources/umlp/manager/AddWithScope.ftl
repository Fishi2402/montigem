<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static void addWithScope${name}(${name} ${name?uncap_first}, NotificationScope scope)")}

  getSingleton()._doAddWithScope${name}(${name?uncap_first}, scope);
<#else>
  ${cd4c.method("protected abstract void _doAddWithScope${name}(${name} o, NotificationScope scope)")}
</#if>

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<Void> add${name}2(${name} ${name?uncap_first}, NotificationScope scope)")}

  return this.getSingleton()._doAdd${name}2(${name?uncap_first}, scope);
<#else>
  ${cd4c.method("protected abstract Promise<Void> _doAdd${name}2(${name} o, NotificationScope scope)")}
</#if>

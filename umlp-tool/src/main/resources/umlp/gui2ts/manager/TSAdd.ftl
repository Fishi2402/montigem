<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<Void> add${name}(${name} ${name?uncap_first})")}

  return this.getSingleton()._doAdd${name}(${name?uncap_first});
<#else>
  ${cd4c.method("protected Promise<Void> _doAdd${name}(${name} o)")}
  return this._doAdd${name}2(o, NotificationScope.ALL);
</#if>

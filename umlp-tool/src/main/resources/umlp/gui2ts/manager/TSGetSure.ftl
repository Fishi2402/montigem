<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<${name}> get${name}Sure(long gemId)")}

  return this.getSingleton()._doGet${name}Sure(gemId);
<#else>
  ${cd4c.method("protected abstract Promise<${name}> _doGet${name}Sure(long gemId)")}
</#if>
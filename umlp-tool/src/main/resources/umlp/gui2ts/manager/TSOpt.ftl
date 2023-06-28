<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Promise<Optional<${name}>> ${name?uncap_first}Opt(long gemId)")}

  return this.getSingleton()._do${name}Opt(gemId);
<#else>
  ${cd4c.method("protected abstract Promise<Optional<${name}>> _do${name}Opt(long gemId)")}
</#if>
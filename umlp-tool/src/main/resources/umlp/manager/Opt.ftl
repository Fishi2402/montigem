<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static Optional<${name}> ${name?uncap_first}Opt(long gemId)")}

  return getSingleton()._do${name}Opt(gemId);
<#else>
  ${cd4c.method("protected abstract Optional<${name}> _do${name}Opt(long gemId)")}
</#if>
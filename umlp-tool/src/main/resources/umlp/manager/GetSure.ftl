<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static ${name} get${name}Sure(long gemId)")}

  return getSingleton()._doGet${name}Sure(gemId);
<#else>
  ${cd4c.method("protected abstract ${name} _doGet${name}Sure(long gemId)")}
</#if>
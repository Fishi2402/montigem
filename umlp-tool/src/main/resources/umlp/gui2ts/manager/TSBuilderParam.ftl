<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasParam", "managerName")}
<#if hasParam>
  ${cd4c.method("public static ${name}Builder ${name?uncap_first}Builder1(long gemId)")}
  return ${managerName}.getSingleton()._do${name}Builder1(gemId);
<#else>
  ${cd4c.method("protected abstract ${name}Builder _do${name}Builder1(long gemId)")}
</#if>
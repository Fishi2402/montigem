<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasParam")}
<#if hasParam>
  ${cd4c.method("public static ${name}Builder ${name?uncap_first}Builder(long gemId)")}
  return this.getSingleton()._do${name}Builder(gemId);
<#else>
  ${cd4c.method("protected abstract ${name}Builder _do${name}Builder(long gemId)")}
</#if>
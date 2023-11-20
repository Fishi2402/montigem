<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasParam", "managerName")}
<#if hasParam>
  ${cd4c.method("public static ${name}Builder ${name?uncap_first}BuilderFromId(long gemId)")}
  return ${managerName}.getSingleton()._do${name}BuilderFromId(gemId);
<#else>
  ${cd4c.method("protected abstract ${name}Builder _do${name}BuilderFromId(long gemId)")}
</#if>
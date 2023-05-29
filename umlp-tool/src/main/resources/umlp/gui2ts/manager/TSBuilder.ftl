<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasDelegate", "managerName")}
<#if hasDelegate>
  ${cd4c.method("public static ${name}Builder ${name?uncap_first}Builder()")}
  return ${managerName}.getSingleton()._do${name}Builder();
<#else>
  ${cd4c.method("protected ${name}Builder _do${name}Builder()")}
  return ${managerName}.getSingleton()._do${name}Builder1(this._doGetFreeGemId());
</#if>
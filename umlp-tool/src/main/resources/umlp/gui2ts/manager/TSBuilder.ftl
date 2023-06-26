<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasDelegate", "managerName")}
<#if hasDelegate>
  ${cd4c.method("public static Promise<${name}Builder> ${name?uncap_first}Builder()")}
  return ${managerName}.getSingleton()._do${name}Builder();
<#else>
  ${cd4c.method("protected Promise<${name}Builder> _do${name}Builder()")}
  return this._doGetFreeGemId().then(res => ${managerName}.getSingleton()._do${name}Builder1(res));
</#if>
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  <#-- TODO AGe: derived class is not different from the normal one for the GUI generator -->
  ${cd4c.method("public static Promise<${name}> get${name}Sure(long gemId)")}

  return this.getSingleton()._doGet${name}();
<#else>
  ${cd4c.method("protected Promise<${name}> _doGet${name}()")}

  return Promise.resolve(new ${name}());
</#if>
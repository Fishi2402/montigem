<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delegateFlag")}
<#if delegateFlag>
  ${cd4c.method("public static ${name} register${name}(${name} obj)")}

  <#-- Caches the object and returns the canonical instance for its gemId (monticore#5062). -->
  return getSingleton()._doRegister${name}(obj);
<#else>
  ${cd4c.method("protected abstract ${name} _doRegister${name}(${name} obj)")}
</#if>

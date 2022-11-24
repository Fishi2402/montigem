<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}

<#if hasDelegate>
  ${cd4c.method("public static GemList<${type}> create${name?cap_first}(long gemId, int size, List<Long> initialIds)")}
  return getSingleton()._doCreate${name?cap_first}(gemId, size, initialIds);
<#else>
  ${cd4c.method("protected abstract GemList<${type}> _doCreate${name?cap_first}(long gemId, int size, List<Long> initialIds)")}
</#if>
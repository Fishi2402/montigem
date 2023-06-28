<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}

<#if hasDelegate>
  ${cd4c.method("public static GemList<${type}> create${name}(long gemId, int size, List<Long> initialIds)")}
  return getSingleton()._doCreate${name}(gemId, size, initialIds);
<#else>
  ${cd4c.method("protected abstract GemList<${type}> _doCreate${name}(long gemId, int size, List<Long> initialIds)")}
</#if>

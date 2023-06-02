<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}

<#if hasDelegate>
  ${cd4c.method("public static Promise<GemList<${type}>> create${name?cap_first}(long gemId, int size, List<Long> initialIds)")}
  return this.getSingleton()._doCreate${name?cap_first}(gemId, size, initialIds);
<#else>
  ${cd4c.method("protected abstract Promise<GemList<${type}>> _doCreate${name?cap_first}(long gemId, int size, List<Long> initialIds)")}
</#if>
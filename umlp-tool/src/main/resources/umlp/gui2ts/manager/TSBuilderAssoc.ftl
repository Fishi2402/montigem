<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}
<#if hasDelegate>
  ${cd4c.method("public static Promise<GemList<${type}>> create${name?cap_first}Builder(long gemListId, List<Long> initialIds)")}

  return this.getSingleton()._doCreate${name?cap_first}Builder(gemListId, initialIds);
<#else>
  ${cd4c.method("protected abstract Promise<GemList<${type}>> _doCreate${name?cap_first}Builder(long ownerGemID, List<Long> initialIds)")}
</#if>
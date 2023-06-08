<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}
<#if hasDelegate>
  ${cd4c.method("public static GemList<${type}> create${name?cap_first}Builder(long gemListId, List<Long> initialIds)")}

  return this.getSingleton()._doCreate${name?cap_first}Builder(gemListId, initialIds);
<#else>
  ${cd4c.method("protected abstract GemList<${type}> _doCreate${name?cap_first}Builder(long ownerGemID, List<Long> initialIds)")}
</#if>
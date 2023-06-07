<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}
<#if hasDelegate>
  ${cd4c.method("public static GemList<${type}> create${name}Builder(long gemListId, List<Long> initialIds)")}

  return getSingleton()._doCreate${name}Builder(gemListId, initialIds);
<#else>
  ${cd4c.method("protected abstract GemList<${type}> _doCreate${name}Builder(long ownerGemID, List<Long> initialIds)")}
</#if>

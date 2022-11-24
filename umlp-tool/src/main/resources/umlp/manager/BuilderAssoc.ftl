<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasDelegate")}
<#if hasDelegate>
  ${cd4c.method("public static GemListBuilder<${type}> create${name?cap_first}Builder(long gemListId)")}

  return getSingleton()._doCreate${name?cap_first}Builder(gemListId);
<#else>
  ${cd4c.method("protected abstract GemListBuilder<${type}> _doCreate${name?cap_first}Builder(long ownerGemID)")}
</#if>
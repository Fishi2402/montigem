<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasParam")}

<#if hasParam>
  ${cd4c.method("public static GemList<${type}> create${name?cap_first}(long gemListId, List<Long> initialIds)")}
  return getSingleton()._doCreate${name?cap_first}(gemListId, initialIds);
<#else>
  ${cd4c.method("public static GemList<${type}> create${name?cap_first}(long gemListId)")}
  return getSingleton()._doCreate${name?cap_first}(gemListId);
</#if>
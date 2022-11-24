<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasParam")}

<#if hasParam>
  ${cd4c.method("public static GemList<${type}> create${name?cap_first}(long gemId, List<Long> initialIds)")}
  return getSingleton()._doCreate${name?cap_first}(gemId, initialIds);
<#else>
  ${cd4c.method("public static GemList<${type}> create${name?cap_first}(long gemId)")}
  return getSingleton()._doCreate${name?cap_first}(gemId);
</#if>
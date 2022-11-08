<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasIds")}

<#if hasIds>
  ${cd4c.method("protected abstract GemList<${type}> _doCreate${name?cap_first}(long gemId, long gemListId, List<Long> initialIds)")}
<#else>
  ${cd4c.method("protected GemList<${type}> _doCreate${name?cap_first}(long gemId, long gemListId)")}
  return _doCreate${name?cap_first}(gemId, gemListId, Collections.emptyList());
</#if>
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "hasIds")}

<#if hasIds>
  ${cd4c.method("protected GemList<${type}> _doCreate${name?cap_first}(long gemId, List<Long> initialIds)")}
  return _doCreate${name?cap_first}(gemId, initialIds);
<#else>
  ${cd4c.method("protected GemList<${type}> _doCreate${name?cap_first}(long gemId)")}
  return _doCreate${name?cap_first}(gemId, Collections.emptyList());
</#if>
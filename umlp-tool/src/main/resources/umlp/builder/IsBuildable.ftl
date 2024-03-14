<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrNames")}
${cd4c.method("public boolean isBuildable()")}

<#if attrNames?size == 0>
  return true;
<#else>
  return
    <#list attrNames as attr>
      <#assign name=attr?cap_first>
        (check${name}Present() && check${name}()) <#if attr?has_next>&</#if>
      </#list>
      ;
</#if>

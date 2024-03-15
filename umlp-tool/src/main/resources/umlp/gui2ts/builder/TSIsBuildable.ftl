<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrNames")}
${cd4c.method("public boolean isBuildable()")}

<#if attrNames?size == 0>
  return true;
<#else>
  return true &&
    <#list attrNames as attr>
      <#assign name=attr?cap_first>
        (this.check${name}Present() && this.check${name}()) <#if attr?has_next>&&</#if>
      </#list>
      ;
</#if>

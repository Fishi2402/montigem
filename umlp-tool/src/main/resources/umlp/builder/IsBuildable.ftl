<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrList")}
${cd4c.method("public boolean isBuildable()")}

<#if attrList?size == 0>
  return true;
<#else>
  return
    <#list attrList as attr>
      <#assign name=attr.getName()?remove_ending("Present")?cap_first>
        check${name}Present() & check${name}() <#if attr?has_next>&</#if>
      </#list>
      ;
</#if>



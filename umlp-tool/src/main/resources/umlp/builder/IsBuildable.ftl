<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrList")}
${cd4c.method("public boolean isBuildable()")}

<#if attrList?size == 0>
  return true;
<#else>
  return
    <#list attrList as attr>
      <#assign name=attr.getName()?cap_first?remove_ending("Present")>
        check${name}Present() & check${name}()
      </#list>
      ;
</#if>



<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes", "booleans", "className")}
${cd4c.method("public boolean equals(${className?cap_first} ${className?uncap_first})")}

<#if attributes?size == 0>
  return true;
<#else>
    return
    <#list attributes as attribute>
      this._${attribute?uncap_first}.equals(${className?uncap_first}.get${attribute?cap_first}()) &&
    </#list>
    <#list booleans as boolean>
      this._${boolean?uncap_first}.equals(${className?uncap_first}.is${boolean?cap_first}()) &&
    </#list>true;
</#if>

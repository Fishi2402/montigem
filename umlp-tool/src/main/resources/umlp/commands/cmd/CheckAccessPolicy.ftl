<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("types")}
${cd4c.method("public boolean checkAccessPolicy()")}
  <#if types?size == 0>
    return true;
  <#else>
    boolean ret = true;
    <#list types as type>
    ret = ret && new ${type.print()}AccessPolicy().checkPermission(this);
    </#list>
    return ret;
  </#if>


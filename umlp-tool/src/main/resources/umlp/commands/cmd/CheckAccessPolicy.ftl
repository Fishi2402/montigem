<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("checkPolicy", "types")}
${cd4c.method("public boolean checkAccessPolicy()")}
  <#if checkPolicy>
    return true;
  <#else>
    boolean ret = true;
    <#list types as type>
    ret = ret && new ${type.print()}AccessPolicy().checkPermission(this);
    </#list>
    return ret;
  </#if>


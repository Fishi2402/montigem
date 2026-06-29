<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "isStatic")}
<#if isStatic>
${cd4c.method("public static List<${classname}> loadAll${classname}WithIDs(List<Long> gemIDs)")}
  return getSingleton()._doLoadAll${classname}WithIDs(gemIDs);
<#else>
${cd4c.method("protected abstract List<${classname}> _doLoadAll${classname}WithIDs(List<Long> gemIDs)")}
</#if>

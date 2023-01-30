<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSideName", "oneSideType","assocName", "numerusOfId")}
<#assign methodName = otherSideName?cap_first+"Id"+numerusOfId+"For"+oneSideName?cap_first>
${cd4c.method("public void ensureSelect${methodName}Created()")}
  if(select${methodName} == null) {
    select${methodName} = DatabaseAccessManager.prepareStatement("MATCH (n:${oneSideType} {gemId: ?}) <#t>
    <#lt>-[:${otherSideName}]->(m) RETURN m.gemId");
  }

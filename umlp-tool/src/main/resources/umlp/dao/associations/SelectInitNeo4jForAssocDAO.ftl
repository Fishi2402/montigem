<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSideName", "oneSideType","assocName", "numerusOfId")}
<#assign methodName = otherSideName?cap_first+"Id"+numerusOfId+"For"+oneSideName?cap_first>
${cd4c.method("public void ensureSelect${methodName}Created()")}

    select${methodName} = "MATCH (n:${oneSideType} {gemId: $1}) <#t>
    <#lt>-[:${otherSideName}]->(m) RETURN m.gemId";


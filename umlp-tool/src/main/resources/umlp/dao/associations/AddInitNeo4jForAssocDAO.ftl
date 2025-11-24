<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftType", "rightType", "leftName", "rightName", "navigableLeft", "navigableRight")}
${cd4c.method("public void ensureAdd${assocName?cap_first}Created()")}

    add${assocName?cap_first} = "MATCH (l:${leftType} {gemId: $1}) <#t>
    <#lt> MATCH (r:${rightType} {gemId: $2}) <#t>
    <#if navigableRight>
      CREATE (l)-[:${rightName}]->(r) <#t>
    </#if>
    <#if navigableLeft>
      CREATE (l)<-[:${leftName}]-(r) <#t>
    </#if>";


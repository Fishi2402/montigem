<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftType", "rightType", "leftName", "rightName", "navigableLeft", "navigableRight", "leftIsList", "rightIsList")}
${cd4c.method("public void ensureAdd${assocName?cap_first}Created()")}

    add${assocName?cap_first} = "MATCH (l:${leftType} {gemId: $p1}) <#t>
    <#lt> MATCH (r:${rightType} {gemId: $p2}) <#t>
    <#if navigableRight && !rightIsList>
      <#lt> OPTIONAL MATCH (l)-[oldRight:${rightName}]->() DELETE oldRight WITH DISTINCT l, r <#t>
    </#if>
    <#if navigableLeft && !leftIsList>
      <#lt> OPTIONAL MATCH (r)-[oldLeft:${leftName}]->() DELETE oldLeft WITH DISTINCT l, r <#t>
    </#if>
    <#if navigableRight>
      CREATE (l)-[:${rightName}]->(r) <#t>
    </#if>
    <#if navigableLeft>
      CREATE (l)<-[:${leftName}]-(r) <#t>
    </#if>";

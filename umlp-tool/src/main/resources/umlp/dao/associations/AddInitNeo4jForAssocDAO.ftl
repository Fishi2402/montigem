<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftType", "rightType", "leftName", "rightName", "navigableLeft", "navigableRight")}
${cd4c.method("public void ensureAdd${assocName?cap_first}Created()")}
  if(add${assocName?cap_first} == null) {
    add${assocName?cap_first} = DatabaseAccessManager.prepareStatement("MATCH (l:${leftType} {gemId: ?}) <#t>
    <#lt> MATCH (r:${rightType} {gemId: ?}) <#t>
    <#if navigableRight>
      CREATE (l)-[:${rightName}]->(r) <#t>
    </#if>
    <#if navigableLeft>
      CREATE (l)<-[:${leftName}]-(r) <#t>
    </#if>");
  }

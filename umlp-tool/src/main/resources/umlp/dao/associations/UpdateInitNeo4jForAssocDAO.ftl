<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "assocSideName","sourceType", "targetType", "navigableLeft", "navigableRight")}
${cd4c.method("public void ensureUpdate${assocSideName?cap_first}Created()")}
  if(update${assocSideName?cap_first} == null) {
    update${assocSideName?cap_first} = DatabaseAccessManager.prepareStatement("<#t>
<#lt> MATCH(l:${sourceType} {gemId:?}), <#t>
<#lt> (rNew:${targetType} {gemId: ?}) OPTIONAL MATCH (l)-[aOld:${assocSideName}]-(rOld:${targetType} {gemId:?}) <#t>
<#if navigableRight>
    <#lt>  CREATE (l)-[aNew:${assocSideName}]->(rNew) <#t>
</#if>
<#if navigableLeft>
    <#lt>  CREATE (l)<-[bNew:${assocSideName}]-(rNew) <#t>
</#if> <#lt> DELETE aOld");
}

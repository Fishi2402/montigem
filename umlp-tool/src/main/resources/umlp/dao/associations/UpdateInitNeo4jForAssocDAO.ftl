<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "startSideName", "endSideName","sourceType", "targetType", "navigableLeft", "navigableRight")}
${cd4c.method("public void ensureUpdate${endSideName?cap_first}Created()")}
  if(update${endSideName?cap_first} == null) {
    update${endSideName?cap_first} = DatabaseAccessManager.prepareStatement("<#t>
<#lt> MATCH(start:${sourceType} {gemId:?}) OPTIONAL MATCH (start) -[startToEnd:${endSideName}]-> (oldEnd:${targetType} {gemId: ?}),<#t>
<#lt> (start) <-[endToStart:${startSideName}]-(oldEnd) DELETE startToEnd, endToStart WITH start AS start<#t>
<#lt> MATCH(newEnd:${targetType} {gemId:?})<#t>
<#if navigableRight>
    <#lt> CREATE (start)-[newStartToEnd:${endSideName}]->(newEnd) <#t>
</#if>
<#if navigableLeft>
    <#lt> CREATE (start)<-[newEndToStart:${startSideName}]-(newEnd) <#t>
</#if>");
}

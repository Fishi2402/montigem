<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "startSideName", "endSideName","sourceType", "targetType", "navigableStartToEnd", "navigableEndToStart", "startIsList", "endIsList")}
${cd4c.method("public void ensureUpdate${endSideName?cap_first}Created()")}
  if(update${endSideName?cap_first} == null) {
    update${endSideName?cap_first} = "<#t>
<#lt> MATCH(start:${sourceType} {gemId:$p1})<#t>
<#if navigableStartToEnd>
<#if endIsList>
<#lt> OPTIONAL MATCH (start) -[startToEnd:${endSideName}]-> (:${targetType} {gemId: $p2}) DELETE startToEnd WITH DISTINCT start<#t>
<#else>
<#lt> OPTIONAL MATCH (start) -[startToEnd:${endSideName}]-> (:${targetType}) DELETE startToEnd WITH DISTINCT start<#t>
</#if>
</#if>
<#if navigableEndToStart>
<#lt> OPTIONAL MATCH (start) <-[endToStart:${startSideName}]-(:${targetType} {gemId: $p2}) DELETE endToStart WITH DISTINCT start<#t>
</#if>
<#lt> MATCH(newEnd:${targetType} {gemId:$p3})<#t>
<#if navigableEndToStart && !startIsList>
<#lt> OPTIONAL MATCH (newEnd)-[oldStart:${startSideName}]->() DELETE oldStart WITH DISTINCT start, newEnd<#t>
</#if>
<#if navigableStartToEnd>
    <#lt> CREATE (start)-[newStartToEnd:${endSideName}]->(newEnd) <#t>
</#if>
<#if navigableEndToStart>
    <#lt> CREATE (start)<-[newEndToStart:${startSideName}]-(newEnd) <#t>
</#if>";
}

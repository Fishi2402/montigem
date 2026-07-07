<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "startSide","endSide", "startType", "endType", "navigableRight", "navigableLeft")}
${cd4c.method("public void ensureRemove${assocName?cap_first}Created()")}
if(remove${assocName?cap_first} == null) {
remove${assocName?cap_first} = "MATCH(start:${startType} {gemId:$p1}), (end:${endType} {gemId:$p2}) <#t>
<#if navigableRight>
<#lt> OPTIONAL MATCH (start)-[startToEnd:${endSide}]->(end) DELETE startToEnd<#if navigableLeft> WITH DISTINCT start, end</#if><#t>
</#if>
<#if navigableLeft>
<#lt> OPTIONAL MATCH (start)<-[endToStart:${startSide}]-(end) DELETE endToStart<#t>
</#if>";
}

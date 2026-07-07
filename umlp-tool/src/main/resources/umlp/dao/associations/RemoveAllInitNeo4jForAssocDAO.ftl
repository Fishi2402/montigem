<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startSide", "endSide", "startType", "endType", "navigableStartToEnd", "navigableEndToStart")}
${cd4c.method("public void ensureRemoveAllEntriesRelatedTo${startSide?cap_first}Created()")}
  if(removeAllEntriesRelatedTo${startSide?cap_first} == null) {
    removeAllEntriesRelatedTo${startSide?cap_first} = "MATCH(start:${startType} {gemId:$p1})<#t>
<#if navigableStartToEnd>
<#lt> OPTIONAL MATCH (start)-[startToEnd:${endSide}]->(:${endType}) DELETE startToEnd<#if navigableEndToStart> WITH DISTINCT start</#if><#t>
</#if>
<#if navigableEndToStart>
<#lt> OPTIONAL MATCH (start)<-[endToStart:${startSide}]-(:${endType}) DELETE endToStart<#t>
</#if>";
  }

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startSide", "endSide", "assocName", "startType", "endType")}
${cd4c.method("public void ensureRemoveAllEntriesRelatedTo${endSide?cap_first}Created()")}
  if(removeAllEntriesRelatedTo${endSide?cap_first} == null) {
    removeAllEntriesRelatedTo${endSide?cap_first} = DatabaseAccessManager.prepareStatement("MATCH(l:${startType} {gemId:?})<#t>
<#lt>-[startToEnd:${endSide}]-> (end:${endType}), (start) <-[endToStart:${startSide}]-(end) DELETE startToEnd, endToStart");
  }

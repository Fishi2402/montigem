<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocSide")}
${cd4c.method("public void removeAllEntriesRelatedTo${assocSide?cap_first}(long gemId)")}

  ensureRemoveAllEntriesRelatedTo${assocSide?cap_first}Created();
  DatabaseAccessManager4Neo4j.executeWrite(removeAllEntriesRelatedTo${assocSide?cap_first},
    Values.parameters("p1", gemId));

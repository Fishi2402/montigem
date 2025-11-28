<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void remove${association.getName()?cap_first}(long ${association.getLeft().getName()}GemId, long ${association.getRight().getName()}GemId)")}
  ensureRemove${association.getName()?cap_first}Created();
  DatabaseAccessManager4Neo4j.executeWrite(
    remove${association.getName()?cap_first},
    Values.parameters(
      "p1", ${association.getLeft().getName()}GemId,
      "p2", ${association.getRight().getName()}GemId
    )
);

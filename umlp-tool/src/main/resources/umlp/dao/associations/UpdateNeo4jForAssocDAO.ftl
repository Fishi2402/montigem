<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocStart", "assocEnd" )}
${cd4c.method("public void update${assocEnd?cap_first}(long ${assocStart}GemId, long old${assocEnd?cap_first}GemId, long updated${assocEnd?cap_first}GemId)")}
ensureUpdate${assocEnd?cap_first}Created();
DatabaseAccessManager4Neo4j.executeWrite(
  update${assocEnd?cap_first},
  Values.parameters(
    "p1", ${assocStart}GemId, // previous_start_id
    "p2", old${assocEnd?cap_first}GemId,  // previous_end_id
    "p3", updated${assocEnd?cap_first}GemId  // new_end_id
  )
);


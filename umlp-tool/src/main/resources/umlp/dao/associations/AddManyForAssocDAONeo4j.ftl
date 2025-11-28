<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public Boolean addMany${association.getName()?cap_first}(Map<Long, List<Long>> ${association.getName()?cap_first}s)")}
  ensureAdd${association.getName()?cap_first}Created();
  if(${association.getName()?cap_first}s.entrySet().isEmpty()) {
    return true;
  }
  if(${association.getName()?cap_first}s.values().stream().allMatch(v -> v.isEmpty())) {
    return true;
  }
  for (Map.Entry<Long, List<Long>> entry : ${association.getName()?cap_first}s.entrySet()) {
    for (Long rightHandSide : entry.getValue()) {
       DatabaseAccessManager4Neo4j.executeWrite(add${association.getName()?cap_first},
       Values.parameters("p1", entry.getKey(), "p2", rightHandSide));
    }
  }
  return true; // TODO only return true when it worked


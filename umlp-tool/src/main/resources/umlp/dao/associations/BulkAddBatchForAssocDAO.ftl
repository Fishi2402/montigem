<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void bulkBatchAdd${association.getName()?cap_first}(Map<Long, List<Long>> ${association.getName()?cap_first}s)")}
  ensureAdd${association.getName()?cap_first}Created();
  try {
    for (Map.Entry<Long, List<Long>> entry : ${association.getName()?cap_first}s.entrySet()) {
      for (Long rightHandSide : entry.getValue()) {
        add${association.getName()?cap_first}.clearParameters();
        add${association.getName()?cap_first}.setLong(1, entry.getKey());
        add${association.getName()?cap_first}.setLong(2, rightHandSide);
        add${association.getName()?cap_first}.addBatch();
      }
    }
  } catch (SQLException e){
    throw new RuntimeException(e);
  }

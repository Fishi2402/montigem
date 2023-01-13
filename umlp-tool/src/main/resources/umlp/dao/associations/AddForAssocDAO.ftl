<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void add${association.getName()?cap_first}(long ${association.getLeft().getName()}GemId, long ${association.getRight().getName()}GemId, int index)")}
  ensureAdd${association.getName()?cap_first}Created();
  try {
    add${association.getName()?cap_first}.clearParameters();
    add${association.getName()?cap_first}.setLong(1, ${association.getLeft().getName()}GemId);
    add${association.getName()?cap_first}.setLong(2, ${association.getRight().getName()}GemId);
    add${association.getName()?cap_first}.execute();
  }catch (SQLException e){
    throw new RuntimeException(e);
  }

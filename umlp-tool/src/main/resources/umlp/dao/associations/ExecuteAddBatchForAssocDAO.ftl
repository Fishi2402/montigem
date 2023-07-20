<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void executeBatchAdd${association.getName()?cap_first}()")}
  ensureAdd${association.getName()?cap_first}Created();
  try {
    add${association.getName()?cap_first}.executeBatch();
    add${association.getName()?cap_first}.clearBatch();
  } catch (SQLException e){
    throw new RuntimeException(e);
  }

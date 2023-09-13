<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public Boolean executeBatchAdd${association.getName()?cap_first}()")}
  ensureAdd${association.getName()?cap_first}Created();
  try {
    DatabaseAccessManager.getConnection().setAutoCommit(false);
    add${association.getName()?cap_first}.executeBatch();
    DatabaseAccessManager.getConnection().commit();
    add${association.getName()?cap_first}.clearBatch();
    return true;

  } catch (SQLException e){
    try {
      DatabaseAccessManager.getConnection().rollback();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  e.printStackTrace();
  return false;
  }

  finally{
    try {
      DatabaseAccessManager.getConnection().setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }



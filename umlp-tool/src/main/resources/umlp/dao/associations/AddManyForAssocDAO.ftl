<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public Boolean addMany${association.getName()?cap_first}(Map<Long, List<Long>> ${association.getName()?cap_first}s)")}
  ensureAdd${association.getName()?cap_first}Created();
  synchronized(add${association.getName()?cap_first}){
    try {
      DatabaseAccessManager.getConnection().setAutoCommit(false);
      add${association.getName()?cap_first}.clearBatch();
      for (Map.Entry<Long, List<Long>> entry : ${association.getName()?cap_first}s.entrySet()) {
        for (Long rightHandSide : entry.getValue()) {
          add${association.getName()?cap_first}.clearParameters();
          add${association.getName()?cap_first}.setLong(1, entry.getKey());
          add${association.getName()?cap_first}.setLong(2, rightHandSide);
          add${association.getName()?cap_first}.addBatch();
        }
      }
      add${association.getName()?cap_first}.executeBatch();
      DatabaseAccessManager.getConnection().commit();
      return true;

    } catch (SQLException e){
      try {
        DatabaseAccessManager.getConnection().rollback();
      } catch (SQLException ex) {
        Log.error("Error rolling back transaction", ex);
      }
      Log.error("Error in addMany${association.getName()?cap_first}", e);
      return false;
    }

    finally{
      try {
        DatabaseAccessManager.getConnection().setAutoCommit(true);
      } catch (SQLException e) {
        Log.error("Error while enabling autocommit in addMany${association.getName()?cap_first}", e);
      }
    }
  }

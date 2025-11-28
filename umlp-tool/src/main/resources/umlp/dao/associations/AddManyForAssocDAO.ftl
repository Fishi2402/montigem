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
  try(Connection c = DatabaseAccessManager.getConnection()){
    try(PreparedStatement st = c.prepareStatement(add${association.getName()?cap_first})) {
      c.setAutoCommit(false);
      st.clearBatch();
      for (Map.Entry<Long, List<Long>> entry : ${association.getName()?cap_first}s.entrySet()) {
        for (Long rightHandSide : entry.getValue()) {
          st.clearParameters();
          st.setLong(1, entry.getKey());
          st.setLong(2, rightHandSide);
          st.addBatch();
        }
      }
      st.executeBatch();
      c.commit();
      c.setAutoCommit(true);
      return true;
    } catch (SQLException e){
      try {
        c.rollback();
      } catch (SQLException ex) {
        Log.error("Error rolling back transaction", ex);
      }
      Log.error("Error in addMany${association.getName()?cap_first}", e);
      return false;
    }

    finally{
      try {
        c.setAutoCommit(true);
      } catch (SQLException e) {
        Log.error("Error while enabling autocommit in addMany${association.getName()?cap_first}", e);
      }
    }
  } catch(SQLException e) {
    Log.error("Error in addMany${association.getName()?cap_first}", e);
    return false;
  }

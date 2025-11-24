<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public Boolean executeBatchAdd${association.getName()?cap_first}()")}
  ensureAdd${association.getName()?cap_first}Created();
  try(Connection connection = DatabaseAccessManager.getConnection();) {
    try(PreparedStatement st = connection.prepareStatement(add${association.getName()?cap_first})) {
      connection.setAutoCommit(false);
      st.executeBatch();
      connection.commit();
      st.clearBatch();
      return true;

    } catch (SQLException e){
      try {
        connection.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
      return false;
    } finally{
       try {
         connection.setAutoCommit(true);
       } catch (SQLException e) {
         e.printStackTrace();
       }
    }
  } catch(SQLException ex) {
	  ex.printStackTrace();
    return false;
  }



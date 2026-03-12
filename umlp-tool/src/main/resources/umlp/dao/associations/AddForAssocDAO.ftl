<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void add${association.getName()?cap_first}(long ${association.getLeft().getName()}GemId, long ${association.getRight().getName()}GemId, int index)")}
  ensureAdd${association.getName()?cap_first}Created();
    try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(add${association.getName()?cap_first}); ) {
      st.clearParameters();
      st.setLong(1, ${association.getLeft().getName()}GemId);
      st.setLong(2, ${association.getRight().getName()}GemId);
      st.executeUpdate();

    }catch (SQLException e){
      throw new RuntimeException(e);
    }

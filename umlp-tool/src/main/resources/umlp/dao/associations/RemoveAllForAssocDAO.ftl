<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocSide")}
${cd4c.method("public void removeAllEntriesRelatedTo${assocSide?cap_first}(long gemId)")}

  ensureRemoveAllEntriesRelatedTo${assocSide?cap_first}Created();

    try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(removeAllEntriesRelatedTo${assocSide?cap_first})) {
      st.clearParameters();
      st.setLong(1, gemId);
      st.executeUpdate();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }


<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void addManyToBatch${association.getName()?cap_first}(Map<Long, List<Long>> ${association.getName()?cap_first}s)")}
  ensureAdd${association.getName()?cap_first}Created();
  try(Connection c = DatabaseAccessManager.getConnection(); PreparedStatement st = c.prepareStatement(add${association.getName()?cap_first})) {
    for (Map.Entry<Long, List<Long>> entry : ${association.getName()?cap_first}s.entrySet()) {
      for (Long rightHandSide : entry.getValue()) {
        st.clearParameters();
        st.setLong(1, entry.getKey());
        st.setLong(2, rightHandSide);
        st.addBatch();
      }
    }
  } catch (SQLException e){
    throw new RuntimeException(e);
  }

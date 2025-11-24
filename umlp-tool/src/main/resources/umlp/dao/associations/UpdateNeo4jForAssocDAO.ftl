<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocStart", "assocEnd" )}
${cd4c.method("public void update${assocEnd?cap_first}(long ${assocStart}GemId, long old${assocEnd?cap_first}GemId, long updated${assocEnd?cap_first}GemId)")}
ensureUpdate${assocEnd?cap_first}Created();
try(Connection c = DatabaseAccessManager.getConnection(); PreparedStatement st = c.prepareStatement(update${assocEnd?cap_first})) {
  st.clearParameters();
  st.setLong(1, ${assocStart}GemId); // previous_start_id
  st.setLong(2, old${assocEnd?cap_first}GemId); // previous_end_id
  st.setLong(3, updated${assocEnd?cap_first}GemId); // new_end_id
  st.executeUpdate();
}catch (SQLException e){
  throw new RuntimeException(e);
}

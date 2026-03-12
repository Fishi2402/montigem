<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startingSide", "endingSide")}
<#assign methodName = endingSide?cap_first+"IdFor"+startingSide?cap_first>
${cd4c.method("public Optional<Long> select${methodName}(long ${startingSide}GemId)")}
  ensureSelect${methodName?cap_first}Created();
  try (Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(select${methodName})) {
    st.clearParameters();
    st.setLong(1, ${startingSide}GemId);
    Optional<Long> res = Optional.empty();
    ResultSet rs = st.executeQuery();
    if(rs.next()){
      res = Optional.of(rs.getLong(1));
    }
    return res;
  }catch (SQLException e){
    throw new RuntimeException(e);
  }

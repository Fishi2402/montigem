<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSide")}
<#assign methodName = otherSide?cap_first+"IdsFor"+oneSideName?cap_first>
${cd4c.method("public List<Long> select${methodName}(long ${oneSideName}GemId)")}
ensureSelect${methodName?cap_first}Created();

try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(select${methodName})) {
  st.clearParameters();
  st.setLong(1, ${oneSideName}GemId);
  List<Long> res = new ArrayList<>();
    try(ResultSet rs = st.executeQuery()) {
      while(rs.next()){
        res.add(rs.getLong(1));
      }
      return res;
    } catch(SQLException e) {
      throw new RuntimeException(e);
    }
} catch (SQLException e){
  throw new RuntimeException(e);
}


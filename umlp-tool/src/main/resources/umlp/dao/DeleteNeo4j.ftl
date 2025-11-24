<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "associations")}
${cd4c.method("public void delete${typeName}(long gemId)")}

if(delete${typeName} == null){
  delete${typeName} = "MATCH(n:${typeName} {gemId: $1}) DETACH DELETE n";
}

try(Connection connection = DatabaseAccessManager.getConnection(); PreparedStatement st = connection.prepareStatement(delete${typeName})){
  st.clearParameters();
  st.setLong(1, gemId);
  st.executeUpdate();

}catch (SQLException e){
throw new RuntimeException(e);
}

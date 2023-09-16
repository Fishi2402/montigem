<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "associations")}
${cd4c.method("public void delete${typeName}(long gemId)")}
try{
if(delete${typeName} == null || delete${typeName}.isClosed()){
  delete${typeName} = DatabaseAccessManager.prepareStatement("MATCH(n:${typeName} {gemId: ?}) DETACH DELETE n");
}
delete${typeName}.clearParameters();
delete${typeName}.setLong(1, gemId);
delete${typeName}.executeUpdate();

}catch (SQLException e){
throw new RuntimeException(e);
}

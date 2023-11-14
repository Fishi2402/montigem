<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","tableName", "typeName", "associations")}
${cd4c.method("public void delete${typeName}(long gemId)")}
try{
  if(delete${typeName} == null || delete${typeName}.isClosed()){
    delete${typeName} = DatabaseAccessManager.prepareStatement("DELETE FROM ${tableName} WHERE id = ?");
  }
  <#list associations as assocSide, assoc>
      <#if assocSide.isLeft()>
          <#assign otherSide=assoc.getRight().getName()>
      <#else>
          <#assign otherSide=assoc.getLeft().getName()>
      </#if>
  ${cdName}DAOFactory.getAssoc${assoc.getName()?cap_first}DAO().removeAllEntriesRelatedTo${otherSide?cap_first}(gemId);
  </#list>
  delete${typeName}.clearParameters();
  delete${typeName}.setLong(1, gemId);
  delete${typeName}.executeUpdate();

  }catch (SQLException e){
  throw new RuntimeException(e);
}

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocSide", "assocSideSQLName", "sqlTable")}
${cd4c.method("public void ensureRemoveAllEntriesRelatedTo${assocSide?cap_first}Created()")}
  if(removeAllEntriesRelatedTo${assocSide?cap_first} == null) {
    removeAllEntriesRelatedTo${assocSide?cap_first} = DatabaseAccessManager.prepareStatement("DELETE FROM ${sqlTable} WHERE ${assocSide}_id=?");
  }

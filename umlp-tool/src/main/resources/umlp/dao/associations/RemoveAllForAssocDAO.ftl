<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocSide")}
${cd4c.method("public void removeAllEntriesRelatedTo${assocSide?cap_first}(long gemId)")}

  ensureRemoveAllEntriesRelatedTo${assocSide?cap_first}Created();
  try {
    removeAllEntriesRelatedTo${assocSide?cap_first}.clearParameters();
    removeAllEntriesRelatedTo${assocSide?cap_first}.setLong(1, gemId);
    removeAllEntriesRelatedTo${assocSide?cap_first}.execute();
  }catch (SQLException e){
    throw new RuntimeException(e);
  }
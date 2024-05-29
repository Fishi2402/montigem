<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("association")}
${cd4c.method("public void remove${association.getName()?cap_first}(long ${association.getLeft().getName()}GemId, long ${association.getRight().getName()}GemId)")}
  ensureRemove${association.getName()?cap_first}Created();
  synchronized(remove${association.getName()?cap_first}){
    try {
      remove${association.getName()?cap_first}.clearParameters();
      remove${association.getName()?cap_first}.setLong(1, ${association.getLeft().getName()}GemId);
      remove${association.getName()?cap_first}.setLong(2, ${association.getRight().getName()}GemId);
      remove${association.getName()?cap_first}.executeUpdate();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

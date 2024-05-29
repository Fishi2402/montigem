<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocStart", "assocEnd" )}
${cd4c.method("public void update${assocEnd?cap_first}(long ${assocStart}GemId, long old${assocEnd?cap_first}GemId, long updated${assocEnd?cap_first}GemId)")}
  ensureUpdate${assocEnd?cap_first}Created();
  synchronized(update${assocEnd?cap_first}){
    try {
      update${assocEnd?cap_first}.clearParameters();
      update${assocEnd?cap_first}.setLong(1, ${assocStart}GemId); // previous_start_id
      update${assocEnd?cap_first}.setLong(2, old${assocEnd?cap_first}GemId); // previous_end_id
      update${assocEnd?cap_first}.setLong(3, ${assocStart}GemId); // new_start_id
      update${assocEnd?cap_first}.setLong(4, updated${assocEnd?cap_first}GemId); // new_end_id
      update${assocEnd?cap_first}.executeUpdate();
    }catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

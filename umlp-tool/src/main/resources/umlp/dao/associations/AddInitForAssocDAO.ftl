<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftSideSQLName", "rightSideSQLName", "sqlTable")}
${cd4c.method("public void ensureAdd${assocName?cap_first}Created()")}
  if(add${assocName?cap_first} == null) {
    add${assocName?cap_first} = DatabaseAccessManager.prepareStatement(
      "MERGE INTO ${sqlTable} " +
      "USING ( VALUES (?, ?) ) AS src (lhs, rhs) " +
      "ON ${sqlTable}.${leftSideSQLName}_id = src.lhs AND ${sqlTable}.${rightSideSQLName}_id = src.rhs " +
      "WHEN NOT MATCHED THEN " +
      "    INSERT (${leftSideSQLName}_id, ${rightSideSQLName}_id) " +
      "    VALUES (src.lhs, src.rhs)"
    );
  }

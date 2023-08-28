<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocSideName", "startSideSQLName", "endSideSQLName", "sqlTable")}
${cd4c.method("public void ensureUpdate${assocSideName?cap_first}Created()")}
if(update${assocSideName?cap_first} == null) {
  // Updates the existing row or inserts a new row if non exists
  update${assocSideName?cap_first} = DatabaseAccessManager.prepareStatement(
    "MERGE INTO ${sqlTable} as target " +
    "USING ( VALUES (?, ?, ?, ?) ) as src (previous_start_id, previous_end_id, new_start_id, new_end_id) " +
    "ON (target.${startSideSQLName}_id = src.previous_start_id AND target.${endSideSQLName}_id = src.previous_end_id) " +
    "WHEN MATCHED THEN " +
    "  UPDATE SET target.${startSideSQLName}_id = src.new_start_id, target.${endSideSQLName}_id = src.new_end_id " +
    "WHEN NOT MATCHED THEN " +
    "  INSERT (${startSideSQLName}_id, ${endSideSQLName}_id) VALUES (src.new_start_id, src.new_end_id);"
  );
}

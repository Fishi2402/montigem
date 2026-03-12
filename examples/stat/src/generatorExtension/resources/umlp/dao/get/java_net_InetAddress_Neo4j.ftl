${signature("statement", "columnName")}
${statement}.get(${columnName}).isNull()? null : log_mc_jar.db.LoadingFromDBHelper.readIPFromStringWithoutException(${statement}.get(${columnName}).asString())

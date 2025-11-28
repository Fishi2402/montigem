${signature("statement","columnName")}
${statement}.get(${columnName}).isNull()? null : java.time.Instant.ofEpochMilli(${statement}.get(${columnName}).asLong())

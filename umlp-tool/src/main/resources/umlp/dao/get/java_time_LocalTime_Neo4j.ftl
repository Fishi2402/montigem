${signature("statement","columnName")}
Optional.ofNullable((Long) ${statement}.getObject(${columnName})).map(java.time.LocalTime::ofNanoOfDay).orElse(null)

${signature("statement","columnName")}
Optional.ofNullable((Long) ${statement}.getObject(${columnName})).map(java.time.LocalDate::ofEpochDay).orElse(null)

${signature("statement","columnName")}
Optional.ofNullable((Long) ${statement}.getObject(${columnName})).map(x -> java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(x), java.time.ZoneOffset.UTC)).orElse(null)

${signature("statement", "columnName")}
java.time.Duration.of(${statement}.getLong(${columnName}), java.time.temporal.ChronoUnit.NANOS)

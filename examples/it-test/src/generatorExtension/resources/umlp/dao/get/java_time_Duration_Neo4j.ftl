${signature("statement", "columnName")}
${statement}.get(${columnName}).isNull()? null : java.time.Duration.of(${statement}.get(${columnName}).asLong(), java.time.temporal.ChronoUnit.NANOS)

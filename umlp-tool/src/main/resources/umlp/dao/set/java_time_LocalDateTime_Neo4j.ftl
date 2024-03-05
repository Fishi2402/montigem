${signature("statement","attributeName", "index", "isOptional")}
${statement}.setLong(${index}, ${attributeName}  <#if isOptional>.map(x -> x.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()).orElse(null)
  <#else>.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()</#if>)

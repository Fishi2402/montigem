${signature("statement","attributeName", "index", "isOptional")}
${statement}.setLong(${index}, ${attributeName}<#if isOptional>.map(LocalTime::toNanoOfDay).orElse(null)<#else>.toNanoOfDay()</#if>)

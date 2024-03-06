${signature("statement","attributeName", "index", "isOptional")}
${statement}.setObject(${index}, ${attributeName}<#if isOptional>.map(LocalDate::toEpochDay).orElse(null)<#else>.toEpochDay()</#if>);

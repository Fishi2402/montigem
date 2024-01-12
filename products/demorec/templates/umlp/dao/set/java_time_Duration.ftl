${signature("statement","attributeName", "index", "isOptional")}
${statement}.setLong(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>.toNanos())
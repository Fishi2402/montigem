${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setLong(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>.toNanos());
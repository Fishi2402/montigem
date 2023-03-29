${signature("statement","attributeName", "index", "isOptional")}
${statement}.setString(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>.toString())
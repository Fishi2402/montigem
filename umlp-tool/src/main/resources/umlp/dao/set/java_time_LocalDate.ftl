${signature("statement","attributeName", "index", "isOptional")}
${statement}.setObject(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
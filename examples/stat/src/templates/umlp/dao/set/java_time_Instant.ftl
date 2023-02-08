${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setObject(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
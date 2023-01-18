${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setBoolean(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
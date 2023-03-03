${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setFloat(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
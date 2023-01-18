${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setInt(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
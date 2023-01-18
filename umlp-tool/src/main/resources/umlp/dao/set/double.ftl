${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setDouble(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
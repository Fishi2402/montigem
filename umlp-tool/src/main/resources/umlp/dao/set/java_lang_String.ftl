${signature("statementName", "typeName", "attributeName", "index", "isOptional")}
${statementName}.setString(${index}, ${attributeName}<#if isOptional>.orElse(null)</#if>);
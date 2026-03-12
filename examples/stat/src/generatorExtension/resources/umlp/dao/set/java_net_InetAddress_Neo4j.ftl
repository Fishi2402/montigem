${signature("statement","attributeName", "index", "isOptional")}
"${index}", ${attributeName}<#if isOptional>.orElse(null)</#if>.toString()

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "returnType", "variableName", "attrName")}
${cd4c.method("public ${returnType} ${methodName}()")}
    return ${variableName}.get${attrName?cap_first}();

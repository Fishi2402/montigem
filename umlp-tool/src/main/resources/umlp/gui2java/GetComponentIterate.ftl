<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "returnType", "variableName", "attrName")}
${cd4c.method("public ${returnType} ${methodName}(int idx)")}
    return getIterate${variableName}It().get(idx).get${attrName?cap_first}();

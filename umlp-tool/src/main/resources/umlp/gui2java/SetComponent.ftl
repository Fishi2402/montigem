<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "variableName", "attrName")}
${cd4c.method("public void ${methodName}(${attrType} ${attrName})")}
    ${variableName}.set${attrName?cap_first}(${attrName});
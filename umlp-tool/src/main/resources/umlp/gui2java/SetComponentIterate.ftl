<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "variableName", "attrName")}
${cd4c.method("public void ${methodName}(${attrType} ${attrName}, int idx)")}
getIterate${variableName}It().get(idx).set${attrName?cap_first}(${attrName});

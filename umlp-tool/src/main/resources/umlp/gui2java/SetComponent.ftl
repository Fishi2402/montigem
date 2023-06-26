<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "attrType", "attr", "args", "argValueHolder", "condition")}
${cd4c.method("public void ${methodName}(${args})")}
    set${attr?cap_first}(${argValueHolder});

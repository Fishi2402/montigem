<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "attrName", "attrType")}
${cd4c.method("protected ${attrType} invent${name}${attrName?cap_first}()")}

Log.trace("Inventing ${name} ${attrName}", "DummyProvider");
return _invent${attrType?cap_first}();






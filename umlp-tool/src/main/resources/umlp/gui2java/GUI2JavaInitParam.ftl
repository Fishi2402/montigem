<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name")}
${cd4c.method("protected void init${name?cap_first}(${type} val)")}
this.set${name?cap_first}(val);

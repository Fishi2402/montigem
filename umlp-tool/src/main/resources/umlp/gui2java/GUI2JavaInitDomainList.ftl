<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager")}
${cd4c.method("protected void init${name?cap_first}()")}
this.${name} = ${manager}.get${type}List();
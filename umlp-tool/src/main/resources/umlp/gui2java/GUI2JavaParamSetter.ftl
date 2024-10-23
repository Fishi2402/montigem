<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "setter", "initCheck")}
<#assign mcType = type.printType()>

${cd4c.method("public void ${setter}(${mcType} val)")}
this.${initCheck} = true;
this.${name} = val;
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "setter")}
<#assign mcType = type.printType()>

${cd4c.method("protected void ${setter}(${mcType} val)")}
this.${name} = val;
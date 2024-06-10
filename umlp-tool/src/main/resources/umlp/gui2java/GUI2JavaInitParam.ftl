<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "init", "setter", "initList")}
<#assign mcType = type.printType()>

${cd4c.method("protected void ${init}(${mcType} val)")}
this.${setter}(val);
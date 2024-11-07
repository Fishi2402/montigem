<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "setter", "setExternally", "wait")}
<#assign mcType = type.printType()>

${cd4c.method("public void ${setter}(${mcType} val)")}
this.${setExternally} = true;
this.${name} = val;